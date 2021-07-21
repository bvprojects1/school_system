package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CallCenterRepository extends CrudRepository<CallCenter, Long> {


}
