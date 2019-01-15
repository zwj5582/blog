/*
 *  Created by ZhongWenjie on 2019-01-13 16:25
 */

package org.zwj.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zwj.blog.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findAccountByUsernameAndState(String username, String state);

}
