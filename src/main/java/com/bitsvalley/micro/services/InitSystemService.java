package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class InitSystemService {

    @Autowired
    RuntimePropertiesRepository runtimePropertiesRepository;

    public List<RuntimeProperties> initSystem() {
        List<RuntimeProperties> runtimePropertiesList = new ArrayList<RuntimeProperties>();
        if (runtimePropertiesRepository.findAll() == null
                || !runtimePropertiesRepository.findAll().iterator().hasNext()) {

            RuntimeProperties businessName = new RuntimeProperties();
            businessName.setPropertyName("Business Name");
            businessName.setPropertyValue("bitsvalley");
            runtimePropertiesList.add(businessName);

            RuntimeProperties logo = new RuntimeProperties();
            logo.setPropertyName("logo");
            logo.setPropertyValue("/images/logo.png");
            runtimePropertiesList.add(logo);

            RuntimeProperties address = new RuntimeProperties();
            address.setPropertyName("Address");
            address.setPropertyValue("123 Main street");
            runtimePropertiesList.add(address);

            RuntimeProperties telephone1 = new RuntimeProperties();
            telephone1.setPropertyName("Telephone1");
            telephone1.setPropertyValue("675 879 345");
            runtimePropertiesList.add(telephone1);

            RuntimeProperties telephone2 = new RuntimeProperties();
            telephone2.setPropertyName("Telephone2");
            telephone2.setPropertyValue("665 879 345");
            runtimePropertiesList.add(telephone2);

            RuntimeProperties email = new RuntimeProperties();
            email.setPropertyName("email");
            email.setPropertyValue("info@bitsvalley.com");
            runtimePropertiesList.add(email);

            RuntimeProperties website = new RuntimeProperties();
            website.setPropertyName("website");
            website.setPropertyValue("www.bitsvalley.com");
            runtimePropertiesList.add(website);

            RuntimeProperties fax = new RuntimeProperties();
            fax.setPropertyName("fax");
            fax.setPropertyValue("665 879 345");
            runtimePropertiesList.add(fax);

            Iterable<RuntimeProperties> savingAccountTypeListIterable = runtimePropertiesList;
            runtimePropertiesRepository.saveAll(savingAccountTypeListIterable);
        }
        return runtimePropertiesList;
    }

    public RuntimeSetting  findAll() {
        Iterable<RuntimeProperties> all = runtimePropertiesRepository.findAll();
        Iterator<RuntimeProperties> iterator = all.iterator();
        RuntimeSetting runtime = new RuntimeSetting();
        while (iterator.hasNext()) {
            RuntimeProperties rp = iterator.next();
            if (rp.getPropertyName().equals("Business Name")) {
                runtime.setBusinessName(rp.getPropertyValue());
            } else if (rp.getPropertyName().equals("logo")) {
                runtime.setLogo(rp.getPropertyValue());
            }
        }
        return runtime;
    }

}
