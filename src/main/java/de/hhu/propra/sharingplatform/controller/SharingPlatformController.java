package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.dao.ItemSaleRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SharingPlatformController {

    @Autowired
    private ItemRentalRepo itemRentalRepo;

    @Autowired
    private ItemSaleRepo itemSaleRepo;

    @Autowired
    private ItemService itemService;

    @GetMapping("/")
    public String mainPage(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        model.addAttribute("user", user);
        model.addAttribute("itemRentals", itemRentalRepo.findAll());
        return "mainpage";
    }

    @PostMapping("/")
    public String salePage(Model model, Principal principal, String search) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        List<String> keywords = itemService.searchKeywords(search);
        model.addAttribute("user", user);
        model.addAttribute("keywords", keywords);
        model.addAttribute("itemRentals", itemService.filterRental(keywords));
        return "mainpage";
    }

    @GetMapping("/sale")
    public String SaleMainPage(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        model.addAttribute("user", user);
        model.addAttribute("itemSales", itemSaleRepo.findAll());
        return "salepage";
    }

    @PostMapping("/sale")
    public String SaleMainPage(Model model, Principal principal, String search) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        List<String> keywords = itemService.searchKeywords(search);
        model.addAttribute("user", user);
        model.addAttribute("keywords", keywords);
        model.addAttribute("itemSales", itemService.filterSale(keywords));
        return "salepage";
    }
}
