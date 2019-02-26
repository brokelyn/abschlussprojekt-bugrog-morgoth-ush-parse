package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.RecommendationService;
import de.hhu.propra.sharingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ItemController extends BaseController {

    private final ItemService itemService;
    private final OfferService offerService;
    private final UserService userService;
    private final RecommendationService recommendationService;


    @Autowired
    public ItemController(ItemService itemService, OfferService offerService,
                          UserService userService, RecommendationService recommendationService) {
        this.itemService = itemService;
        this.offerService = offerService;
        this.userService = userService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/item/details/{itemId}")
    public String detailPage(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("user", userService.fetchUserByAccountName(principal.getName()));
        boolean ownItem = itemService.userIsOwner(item.getId(),
            userService.fetchUserIdByAccountName(principal.getName()));
        model.addAttribute("ownItem", ownItem);
        model.addAttribute("recItems", recommendationService.findRecommendations(itemId));
        return "itemDetails";
    }

    @GetMapping("/item/new")
    public String newItem(Model model, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("item", new Item(user));
        model.addAttribute("user", user);
        return "itemForm";
    }

    @PostMapping("/item/new")
    public String inputItemData(Model model, Item item, Principal principal) {
        itemService
            .persistItem(item, userService.fetchUserIdByAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @PostMapping("/item/remove/{itemId}")
    public String markItemAsRemoved(Model model, @PathVariable long itemId,
                                    Principal principal) {
        offerService.removeOffersFromDeletedItem(itemId);
        itemService.removeItem(itemId, userService.fetchUserIdByAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/edit/{itemId}")
    public String editItem(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("itemId", itemId);
        long userId = userService.fetchUserIdByAccountName(principal.getName());
        model.addAttribute("userId", userId);
        itemService.allowOnlyOwner(item, userId);
        return "itemForm";
    }

    @PostMapping("/item/edit/{itemId}")
    public String editItemData(Model model, Item item,
                               @PathVariable long itemId,
                               Principal principal) {
        long userId = userService.fetchUserIdByAccountName(principal.getName());
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account";
    }
}
