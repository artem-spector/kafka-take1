package com.artem.producer.features;

import com.artem.server.Features;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 03/06/2017
 */
public class ClassInfoProducer extends FeatureDataProducer {

    public ClassInfoProducer() {
        super(Features.CLASS_INFO);
    }

    @Override
    protected void processCommand() {
        if ("getDeclaredMethods".equals(command)) {
            Map<String, Object> res = new HashMap<>();

            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String className = entry.getKey();
                List<String> methodNames = (List<String>) entry.getValue();

                Method[] declaredMethods = new Method[0];
                try {
                    declaredMethods = Class.forName(className).getDeclaredMethods();
                } catch (ClassNotFoundException e) {
                    // no methods
                }

                HashMap<String, List<String>> methodSignatures = new HashMap<String, List<String>>();
                res.put(className, methodSignatures);
                for (String methodName : methodNames) {
                    ArrayList<String> signatures = new ArrayList<String>();
                    methodSignatures.put(methodName, signatures);
                    for (Method mtd : declaredMethods) {
                        if (methodName.equals(mtd.getName())) signatures.add(mtd.toString());
                    }
                }
            }

            setData(100, res);
        }  else {
            invalidCommandError(command);
        }
    }
}
