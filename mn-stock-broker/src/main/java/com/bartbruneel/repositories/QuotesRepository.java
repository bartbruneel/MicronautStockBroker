package com.bartbruneel.repositories;

import com.bartbruneel.entities.QuoteEntity;
import com.bartbruneel.entities.SymbolEntity;
import com.bartbruneel.models.QuoteDto;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Integer> {

    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity symbol);

    // Ordering
    List<QuoteDto> listOrderByVolumeDesc();

    List<QuoteDto> listOrderByVolumeAsc();

    // Filter
    List<QuoteDto> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume, Pageable pageable);

    Slice<QuoteDto> list(Pageable pageable);

}
