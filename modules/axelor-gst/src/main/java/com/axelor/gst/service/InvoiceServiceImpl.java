package com.axelor.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.axelor.gst.db.Address;
import com.axelor.gst.db.Company;
import com.axelor.gst.db.Contact;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.repo.AddressRepository;
import com.axelor.gst.db.repo.CompanyRepository;
import com.axelor.gst.db.repo.ContactRepository;
import com.axelor.gst.db.repo.PartyRepository;
import com.axelor.gst.db.repo.SequenceRepository;
import com.google.inject.persist.Transactional;

public class InvoiceServiceImpl implements InvoiceService {

  @Inject CompanyRepository companyRepository;
  @Inject SequenceRepository sequenceRepository;
  @Inject InvoiceLineService invoiceLineService;
  @Inject PartyRepository partyRepository;

  @Override
  @Transactional
  public Contact getContact(Invoice invoice) {
    try {
      Contact contact =
          invoice
              .getParty()
              .getContactList()
              .stream()
              .filter(a -> a.getType().equals(ContactRepository.TYPE_PRIMARY))
              .findFirst()
              .get();
      return contact;
    } catch (Exception e) {
      System.err.println("null contact");
    }
    return null;
  }

  @Override
  @Transactional
  public Address getInvoiceAddress(Invoice invoice) {

    try {
      Address address =
          invoice
              .getParty()
              .getAddressList()
              .stream()
              .filter(
                  a ->
                      a.getType().equals(AddressRepository.TYPE_DEFAULT)
                          || a.getType().equals(AddressRepository.TYPE_INVOICE))
              .findFirst()
              .get();
      return address;

    } catch (Exception e) {
      System.err.println("null invoice address");
      return null;
    }
  }

  @Override
  @Transactional
  public Address getShippingAddress(Invoice invoice) {
    try {
      Address address =
          invoice
              .getParty()
              .getAddressList()
              .stream()
              .filter(
                  a ->
                      a.getType().equals(AddressRepository.TYPE_DEFAULT)
                          || a.getType().equals(AddressRepository.TYPE_SHIPPING))
              .findFirst()
              .get();
      return address;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Company setDefaultCompany(Invoice invoice) {

    Company company = companyRepository.all().fetchOne();
    return company;
  }

  @Override
  public Invoice calculateRates(Invoice invoice) {

    try {

      List<InvoiceLine> invoiceLine = invoice.getInvoiceItemsList();
      BigDecimal netAmount = BigDecimal.ZERO,
          igst = BigDecimal.ZERO,
          cgst = BigDecimal.ZERO,
          sgst = BigDecimal.ZERO,
          grossAmount = BigDecimal.ZERO;

      for (InvoiceLine rate : invoiceLine) {
        netAmount = netAmount.add(rate.getNetAmount());
        cgst = cgst.add(rate.getCgst());
        igst = igst.add(rate.getIgst());
        sgst = sgst.add(rate.getSgst());
        grossAmount = grossAmount.add(rate.getGrossAmount());
      }
      invoice.setNetAmount(netAmount);
      invoice.setNetIgst(igst);
      invoice.setNetCsgt(cgst);
      invoice.setNetSgst(sgst);
      invoice.setGrossAmount(grossAmount);

      return invoice;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<InvoiceLine> getInvoiceLineList(Invoice invoice) {
    try {
      List<InvoiceLine> invoiceLineList = invoice.getInvoiceItemsList();
      List<InvoiceLine> updateInvoiceLineList = new ArrayList<InvoiceLine>();
      for (InvoiceLine line : invoiceLineList) {
        invoice = invoiceLineService.getIgstAndSgstAndCgst(invoice, line);
        line.setNetAmount(invoice.getNetAmount());
        line.setIgst(invoice.getNetIgst());
        line.setSgst(invoice.getNetSgst());
        line.setCgst(invoice.getNetSgst());
        line.setGrossAmount(invoice.getGrossAmount());
        updateInvoiceLineList.add(line);
      }
      return updateInvoiceLineList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, String> getDomainIds(Party party) {

    Map<String, String> setIds = new HashMap<String, String>();

    try {
      setIds.put(
          "contacIds",
          party
              .getContactList()
              .stream()
              .map(i -> i.getId())
              .collect(Collectors.toList())
              .toString()
              .replace('[', '(')
              .replace(']', ')'));

      setIds.put(
          "addressIds",
          party
              .getAddressList()
              .stream()
              .map(i -> i.getId())
              .collect(Collectors.toList())
              .toString()
              .replace('[', '(')
              .replace(']', ')'));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return setIds;
  }
}
