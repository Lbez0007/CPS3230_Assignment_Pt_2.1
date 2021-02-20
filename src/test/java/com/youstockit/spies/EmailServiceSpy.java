package com.youstockit.spies;

import com.youstockit.users.Manager;
import com.youstockit.services.EmailService;
import com.youstockit.users.User;

public class EmailServiceSpy implements EmailService {

    public int numCallsSendEmail = 0;

    public int returnNumCalls() {
        return numCallsSendEmail;
    }

    public void sendEmail(User user){
        numCallsSendEmail++;
    }
}
