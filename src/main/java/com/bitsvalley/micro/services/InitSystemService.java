package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
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

    @Autowired
    LedgerAccountRepository ledgerAccountRepository;

    public List<RuntimeProperties> initSystem() {
        List<RuntimeProperties> runtimePropertiesList = new ArrayList<RuntimeProperties>();
        if ( !runtimePropertiesRepository.findAll().iterator().hasNext() ) {

            RuntimeProperties businessName = new RuntimeProperties();
            businessName.setPropertyName("Business Name");
            businessName.setPropertyValue("bitsvalley");
            runtimePropertiesList.add(businessName);

            RuntimeProperties slogan = new RuntimeProperties();
            slogan.setPropertyName("Slogan");
            slogan.setPropertyValue("together we achieve more");
            runtimePropertiesList.add(slogan);

            RuntimeProperties logo = new RuntimeProperties();
            logo.setPropertyName("logo");
            logo.setPropertyValue("/images/logo.png");
            runtimePropertiesList.add(logo);

            RuntimeProperties unionLogo = new RuntimeProperties();
            unionLogo.setPropertyName("unionLogo");
            unionLogo.setPropertyValue("/images/unionLogo.png");
            runtimePropertiesList.add(unionLogo);

            RuntimeProperties address = new RuntimeProperties();
            address.setPropertyName("address");
            address.setPropertyValue("123 Main street");
            runtimePropertiesList.add(address);

            RuntimeProperties telephone1 = new RuntimeProperties();
            telephone1.setPropertyName("telephone1");
            telephone1.setPropertyValue("675 879 345");
            runtimePropertiesList.add(telephone1);

            RuntimeProperties telephone2 = new RuntimeProperties();
            telephone2.setPropertyName("telephone2");
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

            RuntimeProperties logoSize = new RuntimeProperties();
            logoSize.setPropertyName("logoSize");
            logoSize.setPropertyValue("50");
            runtimePropertiesList.add(logoSize);

            RuntimeProperties themeColor = new RuntimeProperties();
            themeColor.setPropertyName("themeColor");
            themeColor.setPropertyValue("green");
            runtimePropertiesList.add(themeColor);

            RuntimeProperties themeColor2 = new RuntimeProperties();
            themeColor2.setPropertyName("themeColor2");
            themeColor2.setPropertyValue("gray");
            runtimePropertiesList.add(themeColor2);

            RuntimeProperties vatPercent = new RuntimeProperties();
            vatPercent.setPropertyName("vatPercent");
            vatPercent.setPropertyValue("0.195");
            runtimePropertiesList.add(vatPercent);

            RuntimeProperties unitSharePrice = new RuntimeProperties();
            unitSharePrice.setPropertyName("unitSharePrice");
            unitSharePrice.setPropertyValue("10000");
            runtimePropertiesList.add(unitSharePrice);

            Iterable<RuntimeProperties> runtimePropertiesListIterable = runtimePropertiesList;
            runtimePropertiesRepository.saveAll(runtimePropertiesListIterable);

        }
        return runtimePropertiesList;
    }


    public RuntimeSetting  findAll() {
        List<RuntimeSetting> list = new ArrayList<RuntimeSetting>();
        Iterable<RuntimeProperties> all = runtimePropertiesRepository.findAll();
        Iterator<RuntimeProperties> iterator = all.iterator();
        RuntimeSetting runtime = new RuntimeSetting();
        while (iterator.hasNext()) {
            RuntimeProperties rp = iterator.next();
            if (rp.getPropertyName().equals("Business Name")) {
                runtime.setBusinessName(rp.getPropertyValue());
            } else if (rp.getPropertyName().equals("slogan")) {
                runtime.setSlogan(rp.getPropertyValue());
            } else if (rp.getPropertyName().equals("logo")) {
                runtime.setLogo(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("unionLogo")) {
                    runtime.setUnionLogo(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("address")) {
                runtime.setAddress(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("telephone")) {
                runtime.setTelephone(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("telephone2")) {
                runtime.setTelephone2(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("email")) {
                runtime.setEmail(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("fax")) {
                runtime.setFax(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("logoSize")) {
                runtime.setLogoSize(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("themeColor")) {
                runtime.setThemeColor(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("themeColor2")) {
                runtime.setThemeColor2(rp.getPropertyValue());
            }else if (rp.getPropertyName().equals("vatPercent")) {
                runtime.setVatPercent(rp.getPropertyValue());
            }else if(rp.getPropertyName().equals("unitSharePrice")){
                runtime.setUnitSharePrice(rp.getPropertyValue());
            }
        }
        return runtime;
    }

    public String findByPropertyName(String logo) {
        return runtimePropertiesRepository.findByPropertyName(logo).getPropertyValue();
    }
}
