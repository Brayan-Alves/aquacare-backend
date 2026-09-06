package br.edu.ifpr.aquacare.service;

import br.edu.ifpr.aquacare.enums.Severidade;

public record Alerta(String mensagem, Severidade severidade) {}
