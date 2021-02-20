package com.youstockit.factories;

import com.youstockit.ProductCatalogue;
import com.youstockit.StockItem;
import com.youstockit.SupplierServer;
import com.youstockit.users.Supplier;

public class SupplierServerProvisioning {

    SupplierServer supplierServer;

    public SupplierServer provideSupplierServer(){
        supplierServer =  new SupplierServer();
        return supplierServer;
    }


}
