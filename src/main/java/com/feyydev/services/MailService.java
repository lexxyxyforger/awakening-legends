package com.feyydev.services;

import com.feyydev.models.Mail;
import com.feyydev.models.Player;
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
