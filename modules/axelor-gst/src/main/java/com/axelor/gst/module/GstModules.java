package com.axelor.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.gst.db.repo.PartyMangamentRepository;
import com.axelor.gst.db.repo.PartyRepository;
import com.axelor.gst.db.repo.ProductMangementRepository;
import com.axelor.gst.db.repo.ProductRepository;
import com.axelor.gst.db.repo.SequenceMangamentRepository;
import com.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceLineServiceImpl;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.InvoiceServiceImpl;
import com.axelor.gst.service.SequenceService;
import com.axelor.gst.service.SequenceServiceImpl;

public class GstModules extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceService.class).to(InvoiceServiceImpl.class);
    bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
    bind(SequenceService.class).to(SequenceServiceImpl.class);
    bind(SequenceRepository.class).to(SequenceMangamentRepository.class);
    bind(PartyRepository.class).to(PartyMangamentRepository.class);
    bind(ProductRepository.class).to(ProductMangementRepository.class);
  }
}
