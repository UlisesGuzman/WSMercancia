package mx.edu.utng.wsmercancia;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etImpuesto;


    private Button btGuardar;
    private Button btListar;

    private Mercancia mercancia = null;

    final String NAMESPACE = "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL = "http://192.168.24.24:8080/WSMercancia/services/MercanciaWS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startComponentes();
    }

    private void startComponentes() {
        etImpuesto = (EditText) findViewById(R.id.txt_impuesto);
        btGuardar = (Button) findViewById(R.id.btn_guardar);
        btListar = (Button) findViewById(R.id.btn_listar);
        btGuardar.setOnClickListener(this);
        btListar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mercancia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {


        if (v.getId() == btGuardar.getId()) {
            try {
                    if (getIntent().getExtras().getString("accion")
                            .equals("modificar")) {
                        TaskWSUpdate tarea = new TaskWSUpdate();
                        tarea.execute();
                        cleanEditTex();
                    }

                } catch (Exception e) {
                    //Cuando no se haya mandado una accion por defecto es insertar.
                    TaskWSInsert mercancia = new TaskWSInsert();
                    mercancia.execute();
                }


        }
        if (btListar.getId() == v.getId()) {
            startActivity(new Intent(MainActivity.this, ListMercancia.class));
        }
    }//fin conClick

    private void cleanEditTex() {

    }


    private class TaskWSInsert extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addMercancia";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            mercancia = new Mercancia();
            mercancia.setProperty(0, 0);
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("mercancia");
            info.setValue(mercancia);
            info.setType(mercancia.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Mercancia", Mercancia.class);

            /* Para serializar flotantes y otros tipos no cadenas o enteros*/
            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response =
                        (SoapPrimitive) envelope.getResponse();
                String res = response.toString();
                if (!res.equals("1")) {
                    result = true;
                }

            } catch (Exception e) {
                Log.e("Error ", e.getMessage());
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                cleanEditTex();
                Toast.makeText(getApplicationContext(),
                        "Registro exitoso.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error al insertar.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }//fin tarea insertar

    private class TaskWSUpdate extends
            AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "editMercancia";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            mercancia = new Mercancia();
            mercancia.setProperty(0, getIntent().getExtras().getString("valor0"));
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("mercancia");
            info.setValue(mercancia);
            info.setType(mercancia.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "Mercancia", mercancia.getClass());

            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml = (SoapPrimitive) envelope
                        .getResponse();
                String res = resultado_xml.toString();

                if (!res.equals("1")) {
                    result = false;
                }

            } catch (HttpResponseException e) {
                Log.e("Error HTTP", e.toString());
            } catch (IOException e) {
                Log.e("Error IO", e.toString());
            } catch (XmlPullParserException e) {
                Log.e("Error XmlPullParser", e.toString());
            }

            return result;

        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getApplicationContext(), "Actualizado OK",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error al actualizar",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void obtenerDatos(){
        mercancia.setProperty(1, etImpuesto.getText().toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {

            etImpuesto.setText(datosRegreso.getString("valor1"));




        } catch (Exception e) {
            Log.e("Error al Regargar", e.toString());
        }

    }

}