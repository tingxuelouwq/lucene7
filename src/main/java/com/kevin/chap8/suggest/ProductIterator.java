package com.kevin.chap8.suggest;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 类名: ProductIterator<br/>
 * 包名：com.kevin.chap8.suggest<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/30 10:51<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class ProductIterator implements InputIterator {

    private Iterator<Product> productIterator;
    private Product currentProduct;

    public ProductIterator(Iterator<Product> productIterator) {
        this.productIterator = productIterator;
    }

    @Override
    public long weight() {
        return currentProduct.getNumberSold();
    }

    /**
     * 将Product对象序列化存入payload
     * @return
     */
    @Override
    public BytesRef payload() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(currentProduct);
            out.close();
            return new BytesRef(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Well that's unfortunate");
        }
    }

    @Override
    public boolean hasPayloads() {
        return true;
    }

    /**
     * 把产品销售区域存入context
     * @return
     */
    @Override
    public Set<BytesRef> contexts() {
        try {
            Set<BytesRef> regions = new HashSet<>();
            for (String region : currentProduct.getRegions()) {
                regions.add(new BytesRef(region.getBytes("UTF8")));
            }
            return regions;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't convert to UTF-8");
        }
    }

    @Override
    public boolean hasContexts() {
        return true;
    }

    @Override
    public BytesRef next() throws IOException {
        if (productIterator.hasNext()) {
            currentProduct = productIterator.next();
            return new BytesRef(currentProduct.getName().getBytes("UTF8"));
        }
        return null;
    }
}
