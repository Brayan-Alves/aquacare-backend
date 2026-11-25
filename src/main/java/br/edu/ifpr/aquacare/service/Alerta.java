package br.edu.ifpr.aquacare.service;

import br.edu.ifpr.aquacare.enums.Parametro;
import br.edu.ifpr.aquacare.enums.Severidade;

public record Alerta(Parametro parametro, String mensagem, Severidade severidade, String recomendacao) {}
