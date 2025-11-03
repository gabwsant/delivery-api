package com.deliverytech.delivery_api.dto.projection;

import java.math.BigDecimal;

public interface RelatorioVendasRestaurante {
    String getRestauranteNome();
    BigDecimal getTotalVendas();
}