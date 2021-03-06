package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.dao.ItemSaleRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SharingPlatformController extends BaseController {

    private final ItemService itemService;

    private final ItemRentalRepo itemRentalRepo;

    private final ItemSaleRepo itemSaleRepo;

    @Autowired
    public SharingPlatformController(UserService userService, ItemRentalRepo itemRentalRepo,
        ItemSaleRepo itemSaleRepo, ItemService itemService) {
        super(userService);
        this.itemRentalRepo = itemRentalRepo;
        this.itemSaleRepo = itemSaleRepo;
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String mainPage(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        model.addAttribute("user", user);
        model.addAttribute("itemRentals", itemRentalRepo.findAllByDeletedIsFalse());
        return "mainpage";
    }

    @PostMapping("/")
    public String mainPageSearch(Model model, Principal principal, String search, String btn) {
        User user = null;
        if (principal != null) {
            user = new User();
        }

        List<String> keywords = itemService.searchKeywords(search);

        model.addAttribute("user", user);
        model.addAttribute("keywords", keywords);
        model.addAttribute("search", String.join(" ", keywords));
        if (btn.equals("rental")) {
            model.addAttribute("itemRentals",
                itemService.filterKeywords(itemRentalRepo, keywords));
            return "mainpage";
        } else {
            model.addAttribute("itemSales",
                itemService.filterKeywords(itemSaleRepo, keywords));
            return "salepage";
        }
    }

    @GetMapping("/sale")
    public String saleMainPage(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        model.addAttribute("user", user);
        model.addAttribute("itemSales", itemSaleRepo.findAllByDeletedIsFalse());
        return "salepage";
    }
}
