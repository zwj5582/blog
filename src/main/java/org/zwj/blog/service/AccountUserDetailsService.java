/*
 *  Created by ZhongWenjie on 2019-01-13 16:23
 */

package org.zwj.blog.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.zwj.blog.entity.Account;
import org.zwj.blog.utils.Util;

@Component
public class AccountUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = accountService.findByUsernameWithEnabled(username);
        if (!Util.valid(account))
            throw new UsernameNotFoundException(username);
        return new User(account.getUsername(),account.getPassword(), Lists.newArrayList());
    }
}
