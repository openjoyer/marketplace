package com.openjoyer.paymentservice.model;

public enum TransactionType {
    USER_CREDIT, // пополнение счета
    USER_HOLD, // удержание средств
    USER_UNHOLD, // отмена удержания средств (оплата заказа)
    USER_RETURN, // возврат средств
    USER_TRANSFER, // перевод (p2p)
    USER_ADJUSTMENT, // ручное изменение (админами в случае ошибки и тп)
    USER_EXCHANGE, // обмен валюты

    SELLER_CREDIT,
    SELLER_PAYMENT,
    SELLER_INCOME, // прибыль от заказа
    SELLER_RETURN, // возврат средств пользователю
    SELLER_TRANSFER,
    SELLER_ADJUSTMENT,
    SELLER_EXCHANGE,

    // USER_UNHOLD создается сразу вместе с SELLER_INCOME (одно и то же, но для разных пользователей)
    // USER_RETURN вместе с SELLER_RETURN (у одного забирают деньги, другому начисляют)
}
