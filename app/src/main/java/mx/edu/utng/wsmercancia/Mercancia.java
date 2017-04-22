package mx.edu.utng.wsmercancia;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class Mercancia implements KvmSerializable {

    private int id;
    private int impuesto;


    public Mercancia(int id, int impuesto) {
        this.id = id;
        this.impuesto = impuesto;
    }

    public Mercancia() {
        this(0,0);
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return id;
            case 1:
                return impuesto;

        }

        return  null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                id =Integer.parseInt(o.toString());
                break;
            case 1:
                impuesto= Integer.parseInt(o.toString());
                break;

        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "impuesto";
                break;

        }


    }
}
