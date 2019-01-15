/*
 *  Created by ZhongWenjie on 2019-01-13 16:25
 */

package org.zwj.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zwj.blog.entity.Account;
import org.zwj.blog.repository.AccountRepository;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account findByUsernameWithEnabled(String username) {
        return accountRepository.findAccountByUsernameAndState(username, "enabled");
    }

}
