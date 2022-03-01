package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.services.InitSystemService;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class UploadController extends SuperController{

    @Autowired
    UserService userService;

    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    InitSystemService initSystemService;

    @Autowired
    RuntimePropertiesRepository runtimePropertiesRepository;

//    private final String UPLOAD_DIR = "/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/";
    private final String UPLOAD_DIR = "c:/images/";

    @GetMapping("/file")
    public String uploadFile(){
        return "upload";
    }

    @GetMapping(value = "/fileLogo/{userName}")
    public String uploadLogoUsername(@PathVariable("userName") String userName, ModelMap model, HttpServletRequest request) {
        User user = userService.findUserByUserName(userName);
        request.getSession().setAttribute("userName",user.getUserName());
        model.put("userName", user.getUserName());
        return "uploadLogo";
    }

    @GetMapping(value = "/file/{userName}")
    public String uploadFileUsername(@PathVariable("userName") String userName, ModelMap model, HttpServletRequest request) {
        User user = userService.findUserByUserName(userName);
        request.getSession().setAttribute("userName",user.getUserName());
        model.put("userName", user.getUserName());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes,HttpServletRequest request, ModelMap model) {
        String userName = (String)request.getSession().getAttribute("userName");
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }
        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

//        String rpath = request.getRealPath("/");
        String rpath = "";
//        rpath = rpath + "/assets/images/" + imageId; // whatever path you used for storing the file

        String completePAth = rpath + UPLOAD_DIR +"_"+ userName +"_id_"+fileName;
        // save the file on the local file system
        Path path = null;
        try {

            path = Paths.get(completePAth);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        User aUser = userService.findUserByUserName(userName);

        aUser.setIdFilePath(completePAth);
        userService.saveUser(aUser);
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser,false);
        request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
        model.put("user",aUser);
        return "userHome";
    }


    @PostMapping("/uploadLogo")
    public String uploadLogo(@RequestParam("file") MultipartFile file, RedirectAttributes attributes,HttpServletRequest request, ModelMap model) {

        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a logo to upload.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        String completePath = request.getContextPath();
        String completePath = UPLOAD_DIR +fileName;
        RuntimeProperties logo = runtimePropertiesRepository.findByPropertyName("logo");
        if(logo == null){
            logo = new RuntimeProperties();
        }

        logo.setPropertyValue(completePath);
        runtimePropertiesRepository.save(logo);
        Path path = null;
        try {
            path = Paths.get(completePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.put("runtimeSetting",initSystemService.findAll());
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        return "settings";
    }


    @PostMapping("/uploadUnionLogo")
    public String uploadUnionLogo(@RequestParam("file") MultipartFile file, RedirectAttributes attributes,HttpServletRequest request, ModelMap model) {

        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a union logo to upload.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        String rpath = request.getRealPath("/");
        String completePath =  UPLOAD_DIR +fileName;
        RuntimeProperties unionLogo = runtimePropertiesRepository.findByPropertyName("unionLogo");
        unionLogo.setPropertyValue(completePath);
        Path path = null;
        try {
            path = Paths.get(completePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runtimePropertiesRepository.save(unionLogo);
        model.put("runtimeSetting",initSystemService.findAll());
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        return "settings";
    }
}
