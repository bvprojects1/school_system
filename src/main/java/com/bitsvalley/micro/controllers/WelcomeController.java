package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.LoanAccountTransaction;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.BranchRepository;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class WelcomeController extends SuperController{

    @Autowired
    InitSystemService initSystemService;

    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    UserService userService;

    @GetMapping(value = "/")
    public String showIndexPage(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedInUserName());
        return "welcome";
    }


    @GetMapping(value = "/login")
    public String login(ModelMap model, HttpServletRequest request) {
        if(request.getSession() != null){
            request.getSession().invalidate();
        }
        return "login";
    }

    @GetMapping(value = "/welcome")
    public String welcome(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedInUserName());
        User aUser = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        User byUserName = userRepository.findByUserName(getLoggedInUserName());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byUserName);
        request.getSession().setAttribute("runtimeSettings",initSystemService.findAll());

        if(null != aUser){
            SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser, false);
            Collections.reverse(savingBilanzByUserList.getSavingBilanzList()); //TODO: reverse during search?
            if(null == savingBilanzByUserList || savingBilanzByUserList.getSavingBilanzList() == null
                    ||  savingBilanzByUserList.getSavingBilanzList().size() == 0 ){ //first time login
                savingBilanzByUserList = new SavingBilanzList();
                savingBilanzByUserList.setSavingBilanzList(new ArrayList<SavingBilanz>());
                savingBilanzByUserList.setTotalSaving("0");
            }
            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
            return "userHome";
        }else{
            ArrayList<com.bitsvalley.micro.domain.UserRole> customerRole = new ArrayList<com.bitsvalley.micro.domain.UserRole>();
            customerRole.add(userRoleRepository.findByName(com.bitsvalley.micro.utils.UserRole.ROLE_CUSTOMER.name()));
        }
        return "welcome";
    }


    @GetMapping(value = "/welcomeGlobal")
    public String welcomeGlobal(ModelMap model, HttpServletRequest request) {
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE,null);
        return "welcome";
    }

    @GetMapping(value = "/searchCustomer")
    public String searchCustomer(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedInUserName());
        return "welcome";
    }

    @GetMapping(value = "/landing")
    public String showLandingPage(ModelMap model) {
        return "landing";
    }


    @GetMapping(value = "/getImage")
    @ResponseBody
    public byte[] getImage(HttpServletRequest request) throws IOException {
//        String rpath = request.getRealPath("/");
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
//        rpath = rpath + "assets/images/"+user.getIdFilePath(); // whatever path you used for storing the file
        Path path = Paths.get(user.getIdFilePath());
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    @GetMapping(value = "/getLogoImage")
    @ResponseBody
    public byte[] getLogoImage(HttpServletRequest request) throws IOException {
        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
        Path path = Paths.get(runtimeSetting.getLogo());
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    @GetMapping(value = "/getUnionLogoImage")
    @ResponseBody
    public byte[] getUnionLogoImage(HttpServletRequest request) throws IOException {
        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
        Path path = Paths.get(runtimeSetting.getUnionLogo());
        byte[] data = Files.readAllBytes(path);
        return data;
    }


    @CrossOrigin()
    @PostMapping("/landing")
    public String logUserOut( @RequestBody User user) {
        User byUserName = userRepository.findByUserName(user.getUserName());
            if (byUserName.getPassword().equals(user.getPassword())) {
                return "SUCCESS";
            }
        return "FAILURE";
    }

    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request) {
        return "error";
    }
}
