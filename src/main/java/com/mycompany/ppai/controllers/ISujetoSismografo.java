package com.mycompany.ppai.controllers;

import java.util.List;

import com.mycompany.ppai.boundaries.IObservadorSismografo;

public interface ISujetoSismografo {
    public static final List<IObservadorSismografo> observadores = null;
    void subscribir(List<IObservadorSismografo> observador);
    void quitar(IObservadorSismografo observador);
    void notificar();
}
