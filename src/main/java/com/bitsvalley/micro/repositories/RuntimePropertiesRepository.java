package com.bitsvalley.micro.repositories;
import com.bitsvalley.micro.domain.RuntimeProperties;
import org.springframework.data.repository.CrudRepository;

public interface RuntimePropertiesRepository extends CrudRepository<RuntimeProperties, Long> {

    public RuntimeProperties findByPropertyName(String name);

}
