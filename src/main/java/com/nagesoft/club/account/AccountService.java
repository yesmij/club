package com.nagesoft.club.account;

import com.nagesoft.club.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
