package com.kubsu.notification.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportResponseDTO {

    private BigDecimal amount;

    private String mail;
}
