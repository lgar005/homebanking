package com.mindhub.homebanking.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.TransactionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.css.RGBColor;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

@Controller
public class TransactionCotroller {

   @Autowired
   private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping(path = "/api/transactions")
    public ResponseEntity<Object> makeTransaction(Authentication authentication, @RequestParam Double amount, @RequestParam String description,@RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber){
        Client client= clientService.getAuthenticatedClient(authentication);
        //accountRepository.findByNumber(originAccountNumber)
        Account originAccount=accountService.findByNumber(originAccountNumber);
        //accountRepository.findByNumber(destinationAccountNumber)
        Account destinationAccount=accountService.findByNumber(destinationAccountNumber);
        if(client==null){
            return  new ResponseEntity<>("The client was not found", HttpStatus.FORBIDDEN);
        }
        if(amount<=0){
            return  new ResponseEntity<>("Amount must have a value greater than zero", HttpStatus.FORBIDDEN);
        }
        if(amount.isNaN()){
            return  new ResponseEntity<>("Please enter a numerical value", HttpStatus.FORBIDDEN);
        }
        if(description.isBlank()){
            return  new ResponseEntity<>("Please fill in the description", HttpStatus.FORBIDDEN);
        }
        if(originAccountNumber.isBlank()){
            return  new ResponseEntity<>("Please fill in origin account", HttpStatus.FORBIDDEN);
        }
        if(destinationAccountNumber.isBlank()){
            return  new ResponseEntity<>("Please fill in the destination account", HttpStatus.FORBIDDEN);
        }
        if(originAccountNumber.equals(destinationAccountNumber)){
                return  new ResponseEntity<>("The origin and destination account numbers must be different.", HttpStatus.FORBIDDEN);
        }if(originAccount==null){
            return  new ResponseEntity<>("The source account does not exist", HttpStatus.FORBIDDEN);
        }if(originAccount.getClient()!=client){
            return  new ResponseEntity<>("The account does not belong to you", HttpStatus.FORBIDDEN);
        }if(destinationAccount==null){
            return  new ResponseEntity<>("The destination account does not exist", HttpStatus.FORBIDDEN);
        }if(originAccount.getBalance()<amount){
            return  new ResponseEntity<>("The amount to be transferred is greater than the available balance", HttpStatus.FORBIDDEN);
        }else{
            LocalDateTime dateCreated=LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
            String date1= fmt.format(dateCreated);
            LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
            originAccount.setBalance(originAccount.getBalance()-amount);
            destinationAccount.setBalance(destinationAccount.getBalance()+amount);
            Transaction debitTransaction= new Transaction(TransactionType.DEBIT, amount*-1, description + " "+ originAccountNumber, date1f, originAccount.getBalance());
            Transaction creditTransaction= new Transaction(TransactionType.CREDIT, amount, description+" "+ destinationAccountNumber, date1f, destinationAccount.getBalance());
            originAccount.addTransaction(debitTransaction);
            destinationAccount.addTransaction(creditTransaction);
            transactionService.saveTransaction(debitTransaction);
            transactionService.saveTransaction(creditTransaction);

            return new ResponseEntity<>("transaction ", HttpStatus.CREATED);
        }
    }

    @PostMapping("/api/download")
    public ResponseEntity<Object> generatePDF(@RequestParam Long idAccount, @RequestParam String initialDate, @RequestParam String finalDate, Authentication authentication) throws ParseException, IOException, DocumentException {
        Client client= clientService.getAuthenticatedClient(authentication);
        Account account=accountService.findById(idAccount);
        Date initialDate1= TransactionsUtils.stringtoDate(initialDate);
        Date finalDate1= TransactionsUtils.stringtoDate(finalDate);
        LocalDateTime initialDate2= TransactionsUtils.dateToLocalDateTime(initialDate1);
        LocalDateTime finalDate2= TransactionsUtils.dateToLocalDateTime(finalDate1).plusDays(1).minusSeconds(1);
        if(initialDate.isBlank()){
            return  new ResponseEntity<>("No initial date has been selected", HttpStatus.FORBIDDEN);
        }
        if(finalDate.isBlank()){
            return  new ResponseEntity<>("No final date has been selected", HttpStatus.FORBIDDEN);
        }
        if(account.getClient()!=client){
            return  new ResponseEntity<>("This account does not belong to you", HttpStatus.FORBIDDEN);
        }


        Document document= new Document();
        PdfWriter.getInstance(document, new FileOutputStream("transactions.pdf"));
        document.open();

        Image logo= Image.getInstance("C:\\Users\\Hp\\Downloads\\homebanking\\src\\main\\resources\\static\\web\\img\\LogoColor.png");
        logo.setAlignment(Paragraph.ALIGN_CENTER);
        logo.scalePercent(40,40);
        document.add(logo);

        Paragraph nameBank= new Paragraph("Mindhub Brothers Bank");
        nameBank.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(nameBank);

        Paragraph nameTitle=new Paragraph("Transactions from " + initialDate+ " to " + finalDate);
        nameTitle.setAlignment(Paragraph.ALIGN_CENTER);
        nameTitle.setSpacingBefore(15);
        nameTitle.setSpacingAfter(30);
        document.add(nameTitle);

        Paragraph accountHolder= new Paragraph("Account Holder: "+ client.getFirstName() +" "+ client.getLastName());
        accountHolder.setAlignment(Paragraph.ALIGN_LEFT);
        accountHolder.setSpacingAfter(15);
        document.add(accountHolder);

        Paragraph selectedAccount= new Paragraph("Number Account: "+ account.getNumber());
        selectedAccount.setAlignment(Paragraph.ALIGN_LEFT);
        selectedAccount.setSpacingAfter(15);
        document.add(selectedAccount);

        Paragraph accountType= new Paragraph("Account Type: "+ account.getAccountType());
        accountType.setAlignment(Paragraph.ALIGN_LEFT);
        accountType.setSpacingAfter(15);
        document.add(accountType);

        Paragraph creationDate= new Paragraph("Creation Date "+ account.getCreationDate());
        creationDate.setAlignment(Paragraph.ALIGN_LEFT);
        creationDate.setSpacingAfter(100);
        document.add(creationDate);


        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        PdfPCell cell = new PdfPCell(new Paragraph("Amount", font));
        PdfPCell cell1= new PdfPCell(new Paragraph("Description", font));
        PdfPCell cell2= new PdfPCell(new Paragraph("Type", font));
        PdfPCell cell3= new PdfPCell(new Paragraph("Date", font));
        PdfPCell cell4= new PdfPCell(new Paragraph("Balance", font));



        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(171, 155, 176));
        table.addCell(cell);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBackgroundColor(new BaseColor(171, 155, 176));
        table.addCell(cell1);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBackgroundColor(new BaseColor(171, 155, 176));
        table.addCell(cell2);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setBackgroundColor(new BaseColor(171, 155, 176));
        table.addCell(cell3);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setBackgroundColor(new BaseColor(171, 155, 176));
        table.addCell(cell4);

        for(Transaction transaction: transactionService.getTransactionsByDate(initialDate2, finalDate2, account)){
            PdfPCell cellAmount= new PdfPCell(new Paragraph(NumberFormat.getNumberInstance(Locale.US).format(transaction.getAmount())));
            cellAmount.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellAmount);

            PdfPCell cellDescription= new PdfPCell(new Paragraph(transaction.getDescription()));
            cellDescription.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellDescription);

            PdfPCell cellType= new PdfPCell(new Paragraph(String.valueOf(transaction.getType())));
            cellType.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellType);

            PdfPCell cellDate= new PdfPCell(new Paragraph(String.valueOf(transaction.getData())));
            cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellDate);

            PdfPCell cellBalance= new PdfPCell(new Paragraph(NumberFormat.getNumberInstance(Locale.US).format(transaction.getBalance())));
            cellBalance.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellBalance);
        }

        document.add(table);
        document.close();
        return new ResponseEntity<>(HttpStatus.CREATED);

    }


}
