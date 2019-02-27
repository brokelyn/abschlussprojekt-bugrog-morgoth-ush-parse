package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.validation.ItemValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {

    private ImageService itemImageSaver;
    private final UserService userService;
    private final ItemRepo itemRepo;

    public ItemService(UserService userService,
                       ImageService itemImageSaver, ItemRepo itemRepo) {
        this.userService = userService;
        this.itemImageSaver = itemImageSaver;
        this.itemRepo = itemRepo;
    }

    public void persistItem(Item item, long userId) {
        validateItem(item);
        User owner = userService.fetchUserById(userId);
        item.setOwner(owner);
        itemRepo.save(item);

        String imagefilename = "bike-dummy.png";
        if (item.getImage() != null && item.getImage().getSize() > 0) {
            imagefilename =
                "itemRental-" + item.getId() + "." + item.getImageExtension();
            itemImageSaver.store(item.getImage(), imagefilename);
        }

        item.setImageFileName(imagefilename);
        itemRepo.save(item);
    }

    public void removeItem(long itemId, long userId) {
        Item item = findIfPresent(itemId);
        allowOnlyOwner(item, userId);

        if (userIsOwner(item.getId(), userId)) {
            item.setDeleted(true);
            itemRepo.save(item);
        }
    }

    private Item findIfPresent(long itemId) {
        Optional<Item> optional = itemRepo.findById(itemId);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not Found");
        }
        return optional.get();
    }

    public Item findItem(long itemId) {
        Item item = findIfPresent(itemId);
        if (item.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Item was deleted");
        }
        return item;
    }

    public void editItem(Item newItem, long oldItemId, long userId) {
        Item oldItemRental = findItem(oldItemId);
        allowOnlyOwner(oldItemRental, userId);
        validateItem(newItem);

        newItem.setOwner(oldItemRental.getOwner());
        newItem.setId(oldItemRental.getId());
        itemRepo.save(newItem);
    }

    public void allowOnlyOwner(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your Item");
        }
    }

    public boolean userIsOwner(long itemId, long userId) {
        Item item = findIfPresent(itemId);
        return item.getOwner().getId() == userId;
    }

    private void validateItem(Item item) {
        ItemValidator.validateItem(item);
    }

    public List<String> searchKeywords(String search) {
        if (search.equals("")) {
            return new ArrayList<>();
        }
        String[] split = search.toLowerCase()
            .replaceAll(",", " ")
            .replaceAll("-", " ")
            .replaceAll("_", " ")
            .trim().replaceAll(" +", " ")
            .split(" ");

        List<String> keywords = new ArrayList<>();
        for (String word : split) {
            if (!keywords.contains(word)) {
                keywords.add(word);
            }
        }
        return keywords;
    }

    public List<ItemSale> filterSale(List<String> keywords) {
        if (keywords == null || keywords.size() == 0) {
            return (List<ItemSale>) itemRepo.findAll();
        }
        List<ItemSale> items = new ArrayList<>();
        for (String key : keywords) {
            List<ItemSale> searching = itemRepo.findAllByNameContainsIgnoreCase(key);
            items.addAll(searching);
        }
        return items;
    }

    public List<ItemRental> filterRental(List<String> keywords) {
        if (keywords == null || keywords.size() == 0) {
            return (List<ItemRental>) itemRepo.findAll();
        }
        List<ItemRental> items = new ArrayList<>();
        for (String key : keywords) {
            List<ItemRental> searching = itemRepo.findAllByNameContainsIgnoreCase(key);
            items.addAll(searching);
        }
        return items;
    }
}
