package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public void save(Account account) {
        accountRepository.save(account);
    }
}
