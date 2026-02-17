package com.factusapp.repository;

import com.factusapp.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad InvoiceItem
 *
 * @author FactusApp
 * @version 1.0
 */
@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
