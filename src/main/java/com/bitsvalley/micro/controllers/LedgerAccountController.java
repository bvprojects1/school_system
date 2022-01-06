package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
import com.bitsvalley.micro.repositories.SavingAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.GeneralLedgerService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LedgerEntryDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class LedgerAccountController extends SuperController{

    @Autowired
    LedgerAccountRepository ledgerAccountRepository;

    @Autowired
    SavingAccountRepository savingAccountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GeneralLedgerService generalLedgerService;

    @GetMapping(value = "/newLedgerAccount")
    public String newLedgerAccount(ModelMap model, HttpServletRequest request) {

        Iterable<LedgerAccount> all = ledgerAccountRepository.findAll();
        if( !all.iterator().hasNext()){
            //init ledgerAccounts
            List<LedgerAccount> ledgerAccountList = new ArrayList<LedgerAccount>();
//
            LedgerAccount ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SAVINGS);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setCode(BVMicroUtils.SAVINGS+"_"+BVMicroUtils.GL_3003);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);


//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.RETIREMENT_SAVINGS);
//            ledgerAccount.setCategory("3000 – 3999");
//            ledgerAccount.setCode(BVMicroUtils.RETIREMENT_SAVINGS+"_"+BVMicroUtils.GL_3005);
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
//            ledgerAccount.setCreatedDate(new Date());
//            ledgerAccountList.add(ledgerAccount);
//
//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.DAILY_SAVINGS);
//            ledgerAccount.setCategory("3000 – 3999");
//            ledgerAccount.setCode(BVMicroUtils.DAILY_SAVINGS+"_"+BVMicroUtils.GL_3006);
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
//            ledgerAccount.setCreatedDate(new Date());
//            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.LOAN);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setCode(BVMicroUtils.LOAN+"_"+BVMicroUtils.GL_3001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CASH);
            ledgerAccount.setCategory("5000 – 5999");
            ledgerAccount.setCode(BVMicroUtils.CASH+"_"+BVMicroUtils.GL_5001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.LOAN_INTEREST);
            ledgerAccount.setCategory("7000 – 7999");
            ledgerAccount.setCode(BVMicroUtils.LOAN_INTEREST+"_"+BVMicroUtils.GL_7001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.VAT);
            ledgerAccount.setCode(BVMicroUtils.VAT+"_"+BVMicroUtils.GL_4002);
            ledgerAccount.setCategory("4000 – 4999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.NO_NAME);
            ledgerAccount.setCode(BVMicroUtils.NO_NAME+"_"+BVMicroUtils.NO_NAME_0004);
            ledgerAccount.setCategory("4000 – 4999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccount.setCreatedBy(BVMicroUtils.INIT_SYSTEM);
            ledgerAccount.setCreatedDate(new Date());
            ledgerAccountList.add(ledgerAccount);



            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CURRENT);
            ledgerAccount.setCode(BVMicroUtils.CURRENT+"_"+BVMicroUtils.GL_3004);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.GENERAL_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.GENERAL_SAVINGS+"_"+BVMicroUtils.GL_3005);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.RETIREMENT_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.RETIREMENT_SAVINGS+"_"+BVMicroUtils.GL_3006);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.DAILY_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.DAILY_SAVINGS+"_"+BVMicroUtils.GL_3007);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.MEDICAL_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.MEDICAL_SAVINGS +"_"+ BVMicroUtils.GL_3008);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.MEDICAL_SAVINGS);
//            ledgerAccount.setCode(BVMicroUtils.MEDICAL_SAVINGS+"_"+ BVMicroUtils.GL_3009);
//            ledgerAccount.setCategory("3000 – 3999");
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SOCIAL_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.SOCIAL_SAVINGS+"_"+BVMicroUtils.GL_3010);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.BUSINESS_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.BUSINESS_SAVINGS+"_"+BVMicroUtils.GL_3011);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CHILDREN_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.CHILDREN_SAVINGS+"_"+BVMicroUtils.GL_3012);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.REAL_ESTATE_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.REAL_ESTATE_SAVINGS+"_"+BVMicroUtils.GL_3013);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.EDUCATION_SAVINGS);
            ledgerAccount.setCode(BVMicroUtils.EDUCATION_SAVINGS+"_"+BVMicroUtils.GL_3014);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SHORT_TERM_LOAN);
            ledgerAccount.setCode(BVMicroUtils.SHORT_TERM_LOAN+"_"+BVMicroUtils.GL_3015);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CONSUMPTION_LOAN);
            ledgerAccount.setCode(BVMicroUtils.CONSUMPTION_LOAN+"_"+BVMicroUtils.GL_3016);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);


            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.AGRICULTURE_LOAN);
            ledgerAccount.setCode(BVMicroUtils.AGRICULTURE_LOAN+"_"+BVMicroUtils.GL_3017);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.BUSINESS_INVESTMENT_LOAN);
            ledgerAccount.setCode(BVMicroUtils.BUSINESS_INVESTMENT_LOAN+"_"+BVMicroUtils.GL_3018);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SCHOOL_FEES_LOAN);
            ledgerAccount.setCode(BVMicroUtils.SCHOOL_FEES_LOAN+"_"+BVMicroUtils.GL_3019);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.REAL_ESTATE_LOAN);
            ledgerAccount.setCode(BVMicroUtils.REAL_ESTATE_LOAN+"_"+BVMicroUtils.GL_3020);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.OVERDRAFT_LOAN);
            ledgerAccount.setCode(BVMicroUtils.OVERDRAFT_LOAN+"_"+BVMicroUtils.GL_3021);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.NJANGI_FINANCING);
            ledgerAccount.setCode(BVMicroUtils.NJANGI_FINANCING+"_"+BVMicroUtils.GL_3022);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SHARE);
            ledgerAccount.setCode(BVMicroUtils.SHARE+"_"+BVMicroUtils.GL_5023);
            ledgerAccount.setCategory("5000 – 5999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);



            Iterable<LedgerAccount> ledgerListIterable = ledgerAccountList;
            ledgerAccountRepository.saveAll(ledgerListIterable);
        }

        model.put("ledgerAccount",new LedgerAccount());
        model.put("ledgerAccountList", all);
        return "ledgeraccount";
    }

    @PostMapping(value = "/saveLedgerAccountForm")
    public String saveLedgerAccountForm(@ModelAttribute("ledgerAccount") LedgerAccount ledgerAccount,
                                        ModelMap model ) {
        if(null == ledgerAccount.getStatus()) ledgerAccount.setStatus(BVMicroUtils.INACTIVE);
        ledgerAccount.setStatus(ledgerAccount.getStatus().equals("true")?"ACTIVE":"INACTIVE");

        ledgerAccount.setCode(ledgerAccount.getName()+"_"+ledgerAccount.getCode());
        ledgerAccount.setName(ledgerAccount.getCode());
        ledgerAccountRepository.save(ledgerAccount);

//        ledgerAccount = new LedgerAccount();
//        ledgerAccount.setName(BVMicroUtils.CASH_GL_5001);
//        ledgerAccount.setCategory("5000 – 5999");
//        ledgerAccount.setCode(BVMicroUtils.CASH_GL_5001);
//        ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//        ledgerAccountList.add(ledgerAccount);

        model.put("ledgerAccountList", ledgerAccountRepository.findAll());
        model.put("ledgerAccountInfo", "Created "+ledgerAccount.getName()+ " successfully ");
        return "ledgeraccount";
    }

    @GetMapping(value = "/addGeneralLedgerEntry/{id}")
    public String addGeneralLedgerEntry(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        List<LedgerAccount> destinationLedgerAccount = ledgerAccountRepository.findAllExcept(id);
        LedgerAccount originLedgerAccount = ledgerAccountRepository.findById(id).get();

        model.put("ledgerEntryDTO", new LedgerEntryDTO());
        model.put("destinationLedgerAccounts", destinationLedgerAccount);
        model.put("originLedgerAccount", originLedgerAccount);
        model.put("glAddEntryTitle", "Transfer Between GL Account");
        return "glAddEntry";
    }


    @GetMapping(value = "/userHomeLedgerEntry")
    public String userHomeLedgerEntry( ModelMap model, HttpServletRequest request) {
        Iterable<LedgerAccount> originLedgerAccounts = ledgerAccountRepository.findAll();

        model.put("ledgerEntryDTO", new LedgerEntryDTO());
        model.put("originLedgerAccounts", originLedgerAccounts);
        return "glAddEntryToAccounts";

    }


    @PostMapping(value = "/userHomeLedgerEntryForm")
    public String userHomeLedgerEntryForm( ModelMap model, HttpServletRequest request, @ModelAttribute("ledgerEntryDTO") LedgerEntryDTO ledgerEntryDTO) {
//        LedgerEntryDTO newLedgerEntryDTO = new LedgerEntryDTO();
        double fromTotal = 0.0;
        double toTotal = 0.0;
        Date recordDate = null;

        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
                String parameterName = (String) parameterNames.nextElement();
                String paramValue = request.getParameter(parameterName);
                if(StringUtils.equals(parameterName, "recordDate")){
                    recordDate = BVMicroUtils.formatDate(paramValue);
                    ledgerEntryDTO.setRecordDate(recordDate);
                    continue;
                }
                if(parameterName.equals("glAccountAmount")){
                    fromTotal = new Double(paramValue);
                    continue;
                }
                toTotal = toTotal + new Double(paramValue);
                String pair = parameterName+"_"+paramValue;

            ledgerEntryDTO.getParamValueString().add(pair);
            }
            if(toTotal != fromTotal){
                Iterable<LedgerAccount> originLedgerAccounts = ledgerAccountRepository.findAll();
                model.put("error", "Amounts entered do not add up");
                model.put("ledgerEntryDTO", ledgerEntryDTO);
                model.put("originLedgerAccounts", originLedgerAccounts);
                return "glAddEntryToAccounts";
            }else{
                generalLedgerService.updateGLAfterLedgerAccountMultipleAccountEntry(ledgerEntryDTO);
            }
        return "glAddEntryToAccounts";
    }


    @GetMapping(value = "/addGeneralLedgerEntryToAccounts/{id}")
    public String addGeneralLedgerEntryToAccounts(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        final User aUser = userRepository.findById(user.getId()).get();
        final List<SavingAccount> savingAccounts = aUser.getSavingAccount();

        LedgerAccount originLedgerAccount = ledgerAccountRepository.findById(id).get();
        LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
        ledgerEntryDTO.setSavingAccounts(savingAccounts);

        model.put("ledgerEntryDTO",ledgerEntryDTO );
        model.put("originLedgerAccount", originLedgerAccount);
        model.put("glAddEntryTitle", "Transfer from "+originLedgerAccount.getName()+" Account");
        return "glAddEntryToAccounts";
    }

    @GetMapping(value = "/addGeneralLedgerEntryFromAccounts/{id}")
    public String addGeneralLedgerEntryFromAccounts(@PathVariable("id") long id, ModelMap model) {

        LedgerAccount originLedgerAccount = ledgerAccountRepository.findById(id).get();

        model.put("ledgerEntryDTO", new LedgerEntryDTO());
        model.put("destinationLedgerAccount", originLedgerAccount);
        model.put("glAddEntryTitle", "Transfer from "+originLedgerAccount.getName()+" Account");
        return "glAddEntryFromAccounts";
    }


    @GetMapping(value = "/cashToLedgerAccount/{id}")
    public String cashToLedgerAccount(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        LedgerAccount fromLedgerAccount = ledgerAccountRepository.findById(id).get();
        LedgerAccount cashAccount = ledgerAccountRepository.findByName(BVMicroUtils.CASH_GL_5001);

        LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
        ledgerEntryDTO.setCreditOrDebit(BVMicroUtils.CREDIT);
        model.put("ledgerEntryDTO",ledgerEntryDTO );
        model.put("destinationLedgerAccounts", fromLedgerAccount);
        model.put("originLedgerAccount", cashAccount);
        model.put("glAddEntryTitle", " "+ fromLedgerAccount.getName() + " To " +BVMicroUtils.CASH_GL_5001 );
        return "glAddEntry";
    }

    @GetMapping(value = "/cashFromLedgerAccount/{id}")
    public String cashFromLedgerAccount(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        LedgerAccount toLedgerAccount = ledgerAccountRepository.findById(id).get();
        LedgerAccount cashAccount = ledgerAccountRepository.findByName(BVMicroUtils.CASH_GL_5001);
        LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
        ledgerEntryDTO.setCreditOrDebit(BVMicroUtils.DEBIT);
        model.put("ledgerEntryDTO",ledgerEntryDTO );
        model.put("destinationLedgerAccounts", toLedgerAccount);
        model.put("originLedgerAccount", cashAccount);
        model.put("glAddEntryTitle", " "+ BVMicroUtils.CASH_GL_5001 + " To "+ toLedgerAccount.getName());
        return "glAddEntry";
    }

    @GetMapping(value = "/cashFromLedgerAccountToAccounts/{id}")
    public String cashFromLedgerAccountToAccounts(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        LedgerAccount toLedgerAccount = ledgerAccountRepository.findById(id).get();
        LedgerAccount cashAccount = ledgerAccountRepository.findByName(BVMicroUtils.CASH_GL_5001);
        LedgerEntryDTO ledgerEntryDTO = new LedgerEntryDTO();
        ledgerEntryDTO.setCreditOrDebit(BVMicroUtils.DEBIT);
        model.put("ledgerEntryDTO",ledgerEntryDTO );
        model.put("destinationLedgerAccounts", toLedgerAccount);
        model.put("originLedgerAccount", cashAccount);
        model.put("glAddEntryTitle", " "+ BVMicroUtils.CASH_GL_5001 + " To "+ toLedgerAccount.getName());
        return "glAddEntry";
    }



    @PostMapping(value = "/addLedgerEntryFormReviewForm")
    public String addLedgerEntryFormReviewForm(@ModelAttribute("ledgerEntryDTO") LedgerEntryDTO ledgerEntryDTO,
                                        ModelMap model, HttpServletRequest request ) {
        LedgerAccount toAccount = ledgerAccountRepository.findById(ledgerEntryDTO.getDestinationLedgerAccount()).get();
        LedgerAccount fromAccount = ledgerAccountRepository.findById(ledgerEntryDTO.getOriginLedgerAccount()).get();

        model.put("ledgerAccount", ledgerEntryDTO);
        model.put("destinationLedgerAccounts", toAccount );
        model.put("originLedgerAccount", fromAccount );
        model.put("glAddEntryTitle", " "+ fromAccount.getName() + " To "+ toAccount.getName());

        if( StringUtils.isEmpty( ledgerEntryDTO.getCreditOrDebit() )){
            model.put("error", "PLEASE SELECT A TRANSACTION TYPE" );
            return "glAddEntry";
        }
        generalLedgerService.updateManualAccountTransaction(ledgerEntryDTO);

        return "ledgerConfirm";
    }

}
