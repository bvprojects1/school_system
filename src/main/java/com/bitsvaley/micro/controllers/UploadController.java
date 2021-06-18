package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.services.UserService;
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
import java.util.Optional;

@Controller
public class UploadController extends SuperController{

    @Autowired
    UserService userService;

    private final String UPLOAD_DIR = "/Users/frusamachifen/";

    @GetMapping("/file")
    public String uploadFile(){

        return "upload";
    }

    @GetMapping(value = "/file/{userName}")
    public String registerSavingAccountTransaction(@PathVariable("userName") String userName, ModelMap model, HttpServletRequest request) {
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
        String completePAth = UPLOAD_DIR +"_"+ userName +"_id_"+fileName;
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
        model.put("user",aUser);
        return "userDetails";
    }

}
