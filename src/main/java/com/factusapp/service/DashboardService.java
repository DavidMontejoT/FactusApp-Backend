package com.factusapp.service;

import com.factusapp.dto.response.DashboardStatsResponse;
import com.factusapp.model.User;
import com.factusapp.repository.ClientRepository;
import com.factusapp.repository.InvoiceRepository;
import com.factusapp.repository.ProductRepository;
import com.factusapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para estadísticas del Dashboard
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Obtener estadísticas del dashboard para un usuario
     */
    public DashboardStatsResponse getDashboardStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Ventas totales del mes actual
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        Long totalSalesLong = invoiceRepository.sumTotalByUserIdAndDateRange(
                userId, startOfMonth, endOfMonth);

        BigDecimal totalSales = totalSalesLong != null ? new BigDecimal(totalSalesLong) : BigDecimal.ZERO;

        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        // Cantidad de facturas emitidas
        long invoiceCount = invoiceRepository.count();

        // Cantidad de productos
        long productCount = productRepository.count();

        // Cantidad de clientes
        long clientCount = clientRepository.count();

        // Límites del plan
        Long invoicesLimit = (long) user.getMaxInvoices();
        Long invoicesRemaining = invoicesLimit - user.getInvoicesCountMonthly();

        // Calcular cambio porcentual vs mes anterior
        // TODO: Implementar cálculo real comparando con mes anterior
        BigDecimal salesChange = BigDecimal.ZERO;

        return DashboardStatsResponse.builder()
                .totalSales(totalSales)
                .invoiceCount(invoiceCount)
                .productCount(productCount)
                .clientCount(clientCount)
                .salesChange(salesChange)
                .invoicesLimit(invoicesLimit)
                .invoicesRemaining(invoicesRemaining)
                .build();
    }

    /**
     * Obtener últimas N facturas de un usuario
     */
    public List<com.factusapp.dto.response.InvoiceResponse> getRecentInvoices(Long userId, int limit) {
        return invoiceRepository.findRecentByUserId(userId,
                        org.springframework.data.domain.PageRequest.of(0, limit,
                            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(com.factusapp.dto.response.InvoiceResponse::fromInvoice)
                .collect(java.util.stream.Collectors.toList());
    }
}
