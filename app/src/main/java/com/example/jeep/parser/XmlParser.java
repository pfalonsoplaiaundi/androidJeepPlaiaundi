package com.example.jeep.parser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.example.jeep.model.Producto;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public static List<Producto> parse(Context context, int resourceId) {
        List<Producto> productos = new ArrayList<>();
        Producto currentProducto = null;
        String text = null;

        try {
            XmlResourceParser parser = context.getResources().getXml(resourceId);
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("producto".equalsIgnoreCase(tagName)) {
                            currentProducto = new Producto();
                            currentProducto.setImageUrls(new ArrayList<>());
                            // CORREGIDO: Leer el ID como un entero
                            String idStr = parser.getAttributeValue(null, "id");
                            currentProducto.setId(Integer.parseInt(idStr));
                            currentProducto.setCategoria(parser.getAttributeValue(null, "categoria"));
                            productos.add(currentProducto);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (currentProducto != null && text != null && !text.trim().isEmpty()) {
                            if ("alias".equalsIgnoreCase(tagName)) {
                                currentProducto.setAlias(text);
                            } else if ("descripcion".equalsIgnoreCase(tagName)) {
                                currentProducto.setDescripcion(text);
                            } else if ("stock".equalsIgnoreCase(tagName)) {
                                currentProducto.setStock(Integer.parseInt(text));
                            } else if ("pvp".equalsIgnoreCase(tagName)) {
                                currentProducto.setPvp(Double.parseDouble(text));
                            } else if ("imageUrl".equalsIgnoreCase(tagName)) {
                                currentProducto.getImageUrls().add(text);
                            } else if ("precioAnterior".equalsIgnoreCase(tagName)) {
                                currentProducto.setPrecioAnterior(Double.parseDouble(text));
                            } else if ("dctoPorcentual".equalsIgnoreCase(tagName)) {
                                currentProducto.setDctoPorcentual(Integer.parseInt(text));
                            } else if ("dctoAbsoluto".equalsIgnoreCase(tagName)) {
                                currentProducto.setDctoAbsoluto(Double.parseDouble(text));
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException | NumberFormatException e) {
            Log.e("XmlParser", "Error parseando el XML de productos", e);
        }
        return productos;
    }
}
