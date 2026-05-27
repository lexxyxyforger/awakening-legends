package com.feyydev.services;

import com.feyydev.managers.InventoryManager;
import com.feyydev.managers.CharacterManager;
import com.feyydev.models.*;
import com.feyydev.utils.Constants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MailService {
    private static MailService instance;

    private MailService() {}

    public static MailService getInstance() {
        if (instance == null) instance = new MailService();
        return instance;
    }

    public void addMail(Player player, Mail mail) {
        player.getMailbox().add(mail);
    }

    public void addWelcomeMails(Player player) {
        player.getMailbox().addAll(Constants.createWelcomeMail());
    }

    public boolean claimMail(Player player, String mailId) {
        for (Mail mail : player.getMailbox()) {
            if (mail.getId().equals(mailId) && !mail.isClaimed()) {
                applyRewards(player, mail);
                mail.setClaimed(true);
                return true;
            }
        }
        return false;
    }

    public int claimAllMail(Player player) {
        int count = 0;
        for (Mail mail : player.getMailbox()) {
            if (!mail.isClaimed() && !mail.isExpired()) {
                applyRewards(player, mail);
                mail.setClaimed(true);
                count++;
            }
        }
        return count;
    }

    private void applyRewards(Player player, Mail mail) {
        player.addGold(mail.getRewardGold());
        player.addGems(mail.getRewardGems());
        player.addExp(mail.getRewardExp());
        if (mail.getRewardSummonTickets() > 0) {
            var ticket = new Item("summon_ticket", "Summon Ticket", "Material",
                "Used for 1 summon", "Rare", 0);
            ticket.setQuantity(mail.getRewardSummonTickets());
            InventoryManager.getInstance().addItem(ticket);
        }
        if (mail.getRewardShards() > 0) {
            String rar = mail.getSenderName().contains("SSR") ? "SSR" : "SR";
            int shards = mail.getRewardShards();
            for (var c : player.getCharacters()) {
                if (c.getRarity().equals(rar)) {
                    c.setShards(c.getShards() + shards);
                    break;
                }
            }
        }
        if (mail.getRewardItemId() != null && !mail.getRewardItemId().isEmpty()) {
            var items = Constants.createDefaultPotions();
            for (var item : items) {
                if (item.getId().equals(mail.getRewardItemId())) {
                    item.setQuantity(1);
                    InventoryManager.getInstance().addItem(item);
                    break;
                }
            }
        }
    }

    public void cleanExpiredMails(Player player) {
        player.getMailbox().removeIf(Mail::isExpired);
    }

    public int getUnclaimedCount(Player player) {
        return (int) player.getMailbox().stream()
            .filter(m -> !m.isClaimed() && !m.isExpired())
            .count();
    }
}
