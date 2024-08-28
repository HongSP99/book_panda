package com.booksajo.bookPanda.order.constant;

public enum Status {
    PAY_DONE("주문 완료"),
    SHIPPING("배송 중"),
    SHIPPING_DONE("배송 완료"),
    CANCEL("주문 취소");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getlabel() {
        return label;
    }
}
