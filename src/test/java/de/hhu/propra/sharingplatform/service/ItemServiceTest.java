package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ItemServiceTest {

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private ItemRepo itemRepo;

    private Item item;
    private User user;
    private User user2;
    private ItemService itemService;

    @Before
    public void init() {
        itemService = new ItemService(itemRepo, userRepo);

        user = new User();
        user.setName("Test");
        user.setId((long) 1);
        user2 = new User();
        user2.setName("Test2");
        user2.setId((long) 2);

        item = new Item();
        item.setId((long) 1);
        item.setName("TestItem");
        item.setBail(100.0);
        item.setPrice(20.0);
        item.setDescription("This is a test");
        item.setLocation("Test-Location");
    }

    @Test
    public void persistOneValidItem() {
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        when(userRepo.findOneById(1)).thenReturn(user);

        itemService.persistItem(item, 1);

        verify(itemRepo, times(1)).save(argument.capture());
        assert item.equals(argument.getValue());
        assert argument.getValue().getOwner().getId() == 1;
    }

    @Test
    public void removeOneItemValidUser() {
        item.setOwner(user);
        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.removeItem(1, 1);

        assert itemRepo.findOneById(1).isDeleted();
    }

    @Test
    public void removeOneItemInvalidUser() {
        item.setOwner(user);
        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.removeItem(1, 2);

        assert !itemRepo.findOneById(1).isDeleted();
    }

    @Test
    public void dontPersistInvalidItem() {
        item.setLocation(null);
        when(userRepo.findOneById(1)).thenReturn(user);

        itemService.persistItem(item, 1);
        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void getItemValidUser() {
        item.setOwner(user);
        when(itemRepo.findOneById(1)).thenReturn(item);

        Item editItem = itemService.getItem(1, 1);

        assert editItem.equals(item);
    }

    @Test
    public void getItemInvalidUser() {
        item.setOwner(user);
        when(itemRepo.findOneById(1)).thenReturn(item);
        Item editItem = itemService.getItem(1, 2);

        assert editItem == null;
    }

    @Test
    public void editItemValidItemAndUser() {
        item.setOwner(user);
        Item editItem = new Item();
        editItem.setDescription("This is edited");
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 1);

        verify(itemRepo, times(1)).save(argument.capture());
        assert argument.getValue().getDescription().equals(editItem.getDescription());
    }

    @Test
    public void editItemValidItemAndInvalidUser() {
        item.setOwner(user);
        Item editItem = new Item();
        editItem.setDescription("This is edited");
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 2);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void editItemInvalidItemAndValidUser() {
        item.setOwner(user);
        Item editItem = new Item();
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 1);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void setItemInvalidItemAndUser() {
        item.setOwner(user);
        Item editItem = new Item();
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 1);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void findOneItem() {
        when(itemRepo.findOneById(1)).thenReturn(item);
        Item resultItem = itemService.findItem(1);
        assert resultItem.equals(item);
    }
}