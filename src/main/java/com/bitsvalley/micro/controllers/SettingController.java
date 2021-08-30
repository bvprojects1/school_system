package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.repositories.UserRoleRepository;
import com.bitsvalley.micro.services.InitSystemService;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
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
        if(business_name == null){
            business_name = new RuntimeProperties();
            business_name.setPropertyName("Business Name");
        }
        business_name.setPropertyValue(runtimeSetting.getBusinessName());
        list.add(business_name);

        RuntimeProperties address = runtimePropertiesRepository.findByPropertyName("address");
        if(address == null){
            address = new RuntimeProperties();
            address.setPropertyName("address");
        }
        address.setPropertyValue(runtimeSetting.getAddress());
        list.add(address);

        RuntimeProperties logo = runtimePropertiesRepository.findByPropertyName("logo");
        if(logo == null){
            logo = new RuntimeProperties();
            logo.setPropertyName("logo");
        }
        logo.setPropertyValue(runtimeSetting.getLogo());
        list.add(logo);

        RuntimeProperties unionLogo = runtimePropertiesRepository.findByPropertyName("unionLogo");
        if(unionLogo == null){
            unionLogo = new RuntimeProperties();
            unionLogo.setPropertyName("unionLogo");
        }
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
        if(telephone2 == null){
            telephone2 = new RuntimeProperties();
            telephone2.setPropertyName("telephone2");
        }
        telephone2.setPropertyValue(runtimeSetting.getTelephone2());
        list.add(telephone2);

        RuntimeProperties email = runtimePropertiesRepository.findByPropertyName("email");
        if(email == null){
            email = new RuntimeProperties();
            email.setPropertyName("email");
        }
        email.setPropertyValue(runtimeSetting.getEmail());
        list.add(email);

        RuntimeProperties fax = runtimePropertiesRepository.findByPropertyName("fax");
        if(fax == null){
            fax = new RuntimeProperties();
            fax.setPropertyName("fax");
        }
        fax.setPropertyValue(runtimeSetting.getFax());
        list.add(fax);

        RuntimeProperties website = runtimePropertiesRepository.findByPropertyName("website");
        if(website == null){
            website = new RuntimeProperties();
            website.setPropertyName("website");
        }
        website.setPropertyValue(runtimeSetting.getWebsite());
        list.add(website);


        runtimePropertiesRepository.saveAll(list);
        request.getSession().setAttribute("runtimeSettings",initSystemService.findAll());
        model.put("notify", " SETTING SAVED ");
        model.put("runtimeSetting",initSystemService.findAll());
        return "settings";
    }

}
