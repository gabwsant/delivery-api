package com.deliverytech.delivery_api.dto.projection;

import java.math.BigDecimal;

public interface RelatorioFaturamentoCategoria {
    String getCategoria();
    BigDecimal getTotalVendas();
}