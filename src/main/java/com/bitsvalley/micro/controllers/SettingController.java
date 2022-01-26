package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.services.InitSystemService;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class SettingController extends SuperController{


    @Autowired
    InitSystemService initSystemService;

    @Autowired
    RuntimePropertiesRepository runtimePropertiesRepository;

    @GetMapping(value = "/initsettings")
    public String initSystem(ModelMap model, HttpServletRequest request) {
        initSystemService.initSystem();
        model.put("runtimeSetting",initSystemService.findAll());
        return "settings";
    }

    @GetMapping(value = "/viewsettings")
    public String viewSystem(ModelMap model, HttpServletRequest request) {
        model.put("runtimeSetting",initSystemService.findAll());
        return "settings";
    }

    @PostMapping(value = "/saveSettingForm")
    public String postSettingForm(@ModelAttribute("runtimeSetting") RuntimeSetting runtimeSetting, ModelMap model, HttpServletRequest request) {
        List<RuntimeProperties> list = new ArrayList<RuntimeProperties>();

        RuntimeProperties business_name = runtimePropertiesRepository.findByPropertyName("Business Name");
//        if(business_name == null){
//            business_name = new RuntimeProperties();
//            business_name.setPropertyName("Business Name");
//        }
        business_name.setPropertyValue(runtimeSetting.getBusinessName());
        list.add(business_name);

        RuntimeProperties slogan = runtimePropertiesRepository.findByPropertyName("slogan");
        if(slogan == null){
            slogan = new RuntimeProperties();
            slogan.setPropertyName("slogan");
        }
        slogan.setPropertyValue(runtimeSetting.getSlogan());
        list.add(slogan);

        RuntimeProperties address = runtimePropertiesRepository.findByPropertyName("address");
//        if(address == null){
//            address = new RuntimeProperties();
//            address.setPropertyName("address");
//        }
        address.setPropertyValue(runtimeSetting.getAddress());
        list.add(address);

        RuntimeProperties logo = runtimePropertiesRepository.findByPropertyName("logo");
//        if(logo == null){
//            logo = new RuntimeProperties();
//            logo.setPropertyName("logo");
//        }
        logo.setPropertyValue(runtimeSetting.getLogo());
        list.add(logo);

        RuntimeProperties unionLogo = runtimePropertiesRepository.findByPropertyName("unionLogo");
//        if(unionLogo == null){
//            unionLogo = new RuntimeProperties();
//            unionLogo.setPropertyName("unionLogo");
//        }
        unionLogo.setPropertyValue(runtimeSetting.getUnionLogo());
        list.add(unionLogo);

        RuntimeProperties telephone = runtimePropertiesRepository.findByPropertyName("telephone");
        if(telephone == null){
            telephone = new RuntimeProperties();
            telephone.setPropertyName("telephone");
        }
        telephone.setPropertyValue(runtimeSetting.getTelephone());
        list.add(telephone);

        RuntimeProperties telephone2 = runtimePropertiesRepository.findByPropertyName("telephone2");
//        if(telephone2 == null){
//            telephone2 = new RuntimeProperties();
//            telephone2.setPropertyName("telephone2");
//        }
        telephone2.setPropertyValue(runtimeSetting.getTelephone2());
        list.add(telephone2);

        RuntimeProperties email = runtimePropertiesRepository.findByPropertyName("email");
//        if(email == null){
//            email = new RuntimeProperties();
//            email.setPropertyName("email");
//        }
        email.setPropertyValue(runtimeSetting.getEmail());
        list.add(email);

        RuntimeProperties fax = runtimePropertiesRepository.findByPropertyName("fax");
//        if(fax == null){
//            fax = new RuntimeProperties();
//            fax.setPropertyName("fax");
//        }
        fax.setPropertyValue(runtimeSetting.getFax());
        list.add(fax);

        RuntimeProperties website = runtimePropertiesRepository.findByPropertyName("website");
//        if(website == null){
//            website = new RuntimeProperties();
//            website.setPropertyName("website");
//        }
        website.setPropertyValue(runtimeSetting.getWebsite());
        list.add(website);


        RuntimeProperties logoSize = runtimePropertiesRepository.findByPropertyName("logoSize");
//        if(logoSize == null){
//            logoSize = new RuntimeProperties();
//            logoSize.setPropertyName("logoSize");
//        }
        logoSize.setPropertyValue(runtimeSetting.getLogoSize());
        list.add(logoSize);


        RuntimeProperties themeColor = runtimePropertiesRepository.findByPropertyName("themeColor");
//        if(themeColor == null){
//            themeColor = new RuntimeProperties();
//            themeColor.setPropertyName("themeColor");
//        }
        themeColor.setPropertyValue(runtimeSetting.getThemeColor());
        list.add(themeColor);

        RuntimeProperties themeColor2 = runtimePropertiesRepository.findByPropertyName("themeColor2");
//        if(themeColor2 == null){
//            themeColor2 = new RuntimeProperties();
//            themeColor2.setPropertyName("themeColor2");
//        }
        logoSize.setPropertyValue(runtimeSetting.getThemeColor2());
        list.add(themeColor2);


        RuntimeProperties vatPercent = runtimePropertiesRepository.findByPropertyName("vatPercent");
        vatPercent.setPropertyValue(runtimeSetting.getVatPercent().toString());
        list.add(vatPercent);



        runtimePropertiesRepository.saveAll(list);
        request.getSession().setAttribute("runtimeSettings",initSystemService.findAll());
        model.put("notify", " SETTING SAVED ");
        model.put("runtimeSetting",initSystemService.findAll());
        return "settings";
    }

}
