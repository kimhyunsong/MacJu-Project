package com.sib.macju.dto.beer;

import com.sib.macju.domain.beer.Beer;
import com.sib.macju.domain.beer.BeerENMainType;
import com.sib.macju.domain.beer.BeerKOMainType;
import com.sib.macju.domain.member.MemberRateBeer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Data
public class BeerDto implements Serializable {
    private final Long beerId;
    private final BeerTypeDto beerType;
    private final String name;
    private final String englishName;
    private final String content;
    private final Double volume;
    private final String photoPath;
    private final List<String> aromaHashTags;
    private final List<String> flavorHashTags;
    private final Long likes;
    private final Double averageRate;

    @Data
    public static class BeerTypeDto implements Serializable {
        private final BeerKOMainType ko_main;
        private final BeerENMainType en_main;
        private final String ko_detail;
        private final String en_detail;
    }

    public BeerDto(Beer beer) {
        beerId = beer.getBeerId();
        beerType = new BeerTypeDto(beer.getBeerType().getKo_main(), beer.getBeerType().getEn_main(), beer.getBeerType().getKo_detail(), beer.getBeerType().getEn_detail());
        name = beer.getName();
        englishName = beer.getEnglishName();
        content = beer.getContent();
        volume = beer.getVolume();
        photoPath = beer.getPhotoPath();

        aromaHashTags = beer.getBeerHasAromaHashTags()
                .stream()
                .map(beerHasAromaHashTag ->
                        beerHasAromaHashTag.getAromaHashTag().getAroma()
                ).collect(Collectors.toList());

        flavorHashTags = beer.getBeerHasFlavorHashTags()
                .stream()
                .map(beerHasFlavorHashTag ->
                        beerHasFlavorHashTag.getFlavorHashTag().getFlavor()
                ).collect(Collectors.toList());
        likes = beer.getMemberLikeBeers().stream().count();
        OptionalDouble average = beer.getMemberRateBeers().stream().mapToInt(MemberRateBeer::getRate).average();
        if (average.isPresent()){
            averageRate = average.getAsDouble();
        } else{
            averageRate = (double) 0;
        }

    }
}
