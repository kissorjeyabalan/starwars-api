package no.kristiania.pgr301.eksamen.hateos;

import java.util.ArrayList;
import java.util.List;

public class HalPage<T> extends HalObject {
    private List<T> data = new ArrayList<>();
    private int pages = 0;
    private int count = 0;
    private HalLink next = null;
    private HalLink previous = null;
    private HalLink _self = null;

    public HalPage() { }

    public HalPage(List<T> data, int pages, int count, HalLink next, HalLink previous, HalLink _self) {
        this.data = data;
        this.pages = pages;
        this.count = count;
        this.next = next;
        this.previous = previous;
        this._self = _self;
    }
}
