package com.sib.macju.service.member;

import com.sib.macju.domain.beer.Beer;
import com.sib.macju.domain.hashtag.AromaHashTag;
import com.sib.macju.domain.hashtag.FlavorHashTag;
import com.sib.macju.domain.member.*;
import com.sib.macju.domain.post.Post;
import com.sib.macju.dto.beer.BeerDto;
import com.sib.macju.dto.beer.BeerVO;
import com.sib.macju.dto.beer.RateVO;
import com.sib.macju.dto.member.MemberDto;
import com.sib.macju.dto.member.RequestUpdateMemberDto;
import com.sib.macju.dto.post.PostVO;
import com.sib.macju.repository.beer.BeerRepository;
import com.sib.macju.repository.beer.MemberRateBeerRepository;
import com.sib.macju.repository.hashtag.AromaHashTagRepository;
import com.sib.macju.repository.hashtag.FlavorHashTagRepository;
import com.sib.macju.repository.member.*;
import com.sib.macju.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final MemberLikeBeerRepository memberLikeBeerRepository;
    private final MemberLikePostRepository memberLikePostRepository;
    private final MemberRateBeerRepository memberRateBeerRepository;
    private final BeerRepository beerRepository;
    private final PostRepository postRepository;
    private final MemberFondAromaHashTagRepository memberFondAromaHashTagRepository;
    private final MemberFondFlavorHashTagRepository memberFondFlavorHashTagRepository;
    private final AromaHashTagRepository aromaHashTagRepository;
    private final FlavorHashTagRepository flavorHashTagRepository;

    @Override
    public int vaildateMemberNickName(String nickName) {
        Member member = memberRepository.findByNickName(nickName);
        if (member != null) {
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional
    public int signUp(MemberDto memberDto) {
        Member result = null;
        Member member = new Member();
        member.setKakaoId(memberDto.getKakaoId());
        member.setName(memberDto.getName());
        member.setNickName(memberDto.getNickName());
        member.setAge(memberDto.getAge());
        result = memberRepository.save(member);
        if (result == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Member findByMemberId(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        return member;
    }

    @Override
    public Member findByKakaoId(Long kakaoId) {
        Member member = memberRepository.findByKakaoId(kakaoId);
        return member;
    }

    @Override
    public Beer findByBeerId(Long beerId) {
        Optional<Beer> beer = beerRepository.findById(beerId);
        return beer.get();
    }

    @Override
    public Post findByPostId(Long postId) {
        Optional<Post> post = postRepository.findByPostIdAndIs_deletedIsFalse(postId);
        return post.get();
    }

    @Override
    @Transactional
    public int updateProfile(RequestUpdateMemberDto requestUpdateMemberDto) {
        Member member = memberRepository.findByMemberId(requestUpdateMemberDto.getMemberId());
        member.setIntro(requestUpdateMemberDto.getIntro());
        member.setNickName(requestUpdateMemberDto.getNickName());

        member.getMemberFondAromaHashTags().clear();
        member.getMemberFondFlavorHashTags().clear();

        List<MemberFondAromaHashTag> memberFondAromaHashTags =
                requestUpdateMemberDto
                        .getAromas()
                        .stream()
                        .map(
                                aroma -> {
                                    Optional<AromaHashTag> aromaHashTag = aromaHashTagRepository.findById(aroma);
                                    return MemberFondAromaHashTag.createMemberFondAromaHashTag(aromaHashTag.get(), member);
                                }
                        ).collect(Collectors.toList());

        List<MemberFondFlavorHashTag> memberFondFlavorHashTags =
                requestUpdateMemberDto
                        .getFlavors()
                        .stream()
                        .map(flavor -> {
                            Optional<FlavorHashTag> flavorHashTag = flavorHashTagRepository.findById(flavor);
                            return MemberFondFlavorHashTag.createMemberFondFlavorHashTag(flavorHashTag.get(), member);
                        }).collect(Collectors.toList());

        memberFondAromaHashTagRepository.saveAll(memberFondAromaHashTags);
        memberFondFlavorHashTagRepository.saveAll(memberFondFlavorHashTags);

        Member result = memberRepository.save(member);
        if (result.equals(null)) {
            return 0;
        }
        return 1;
    }

    @Override
    @Transactional
    public int withdraw(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        member.setStatus(Status.Deactivate);
        Member result = null;
        result = memberRepository.save(member);
        if (result.equals(null)) {
            return 0;
        }
        return 1;
    }

    @Override
    public List<RateVO> fetchRatedBeer(Long memberId) {
        List<MemberRateBeer> mrbList = memberRateBeerRepository.findAllByMemberId(memberId);
        List<RateVO> result = new ArrayList<>();
        int size = mrbList.size();
        for (int i = 0; i < size; i++) {
            MemberRateBeer mrb = mrbList.get(i);
            RateVO vo = new RateVO();
            vo.setBeer(new BeerDto(findByBeerId(mrb.getBeer().getBeerId())));
            vo.setRate(mrb.getRate());
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<BeerVO> fetchLikedBeer(Long memberId) {
        List<BeerVO> result = new ArrayList<>();
        List<MemberLikeBeer> data = memberLikeBeerRepository.findAllByMember(findByMemberId(memberId));
        int size = data.size();
        for (int i = 0; i < size; i++) {
            Beer beer = data.get(i).getBeer();
            result.add(new BeerVO(beer.getBeerId(), beer.getBeerType().getKo_main().toString(), beer.getName(), beer.getEnglishName(), beer.getContent(), beer.getVolume(), beer.getPhotoPath()));
        }

        return result;

    }

    @Override
    @Transactional
    public int changeBeerLike(Long memberId, Long beerId) {

        MemberLikeBeer mlb = new MemberLikeBeer();
        Member member = findByMemberId(memberId);
        System.out.println(member.toString());
        Beer beer = findByBeerId(beerId);
        Long data = 0L;
        data = memberLikeBeerRepository.findMemberLikeBeerIdByMemberAndBeer(member, beer);
        System.out.println(data);
        if (data != null) {
            //이미 좋아요를 했기 때문에 좋아요 취소 -> delete 처리
            memberLikeBeerRepository.deleteById(data);
            return -1;
        } else {
            //좋아요 처리
            mlb.setMember(member);
            mlb.setBeer(beer);
            MemberLikeBeer result = memberLikeBeerRepository.save(mlb);

            if (result == null) {
                return 0;
            }
            return 1;
        }
    }

    @Override
    public List<PostVO> fetchLikedPost(Long memberId) {
        List<PostVO> result = new ArrayList<>();
        List<MemberLikePost> data = memberLikePostRepository.findAllByMember(findByMemberId(memberId));
        int size = data.size();
        for (int i = 0; i < size; i++) {
            Post post = data.get(i).getPost();
            //BeerDTO 시작
            BeerVO beer = new BeerVO();
            beer.setBeerId(post.getBeer().getBeerId());
            beer.setBeerType(post.getBeer().getBeerType().getKo_main().toString());
            beer.setBeerName(post.getBeer().getName());
            beer.setEnglishName(post.getBeer().getEnglishName());
            beer.setContent(post.getBeer().getContent());
            beer.setVolume(post.getBeer().getVolume());
            beer.setPhotoPath(post.getBeer().getPhotoPath());
            //BeerDTO 끝
            //MemberDTO 시작
            MemberDto member = new MemberDto();
            member.setMemberId(post.getMember().getMemberId());
            member.setNickName(post.getMember().getNickName());
            member.setName(post.getMember().getName());
            member.setAge(post.getMember().getAge());
            member.setGrade(post.getMember().getGrade());
            //MemberDTO 끝
            result.add(new PostVO(post.getPostId(), beer, member, post.getContent(), post.getCreatedAt(), post.getUpdatedAt()));
        }

        return result;
    }

    @Override
    @Transactional
    public int changePostLike(Long memberId, Long postId) {
        MemberLikePost mlp = new MemberLikePost();
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        Long data = 0L;
        data = memberLikePostRepository.findMemberLikePostIdByMemberAndPost(member, post);
        System.out.println(data);
        if (data != null) {
            //이미 좋아요를 했기 때문에 좋아요 취소 -> delete 처리
            memberLikePostRepository.deleteById(data);
            return -1;
        } else {
            //좋아요 처리
            mlp.setMember(member);
            mlp.setPost(post);
            MemberLikePost result = memberLikePostRepository.save(mlp);

            if (result == null) {
                return 0;
            }
            return 1;
        }
    }

    @Override
    @Transactional
    public int changeFollowing(Long memberId, Long followingMemberId) {
        Follow follow = new Follow();
        Member member = findByMemberId(memberId);
        Member followingMember = findByMemberId(followingMemberId);
        Long data = 0L;
        data = followRepository.findIdByFollowerAndFollowing(member, followingMember);
        if (data != null) {
            followRepository.deleteById(data);
            return -1;
        } else {
            follow.setFollower(member);
            follow.setFollowing(followingMember);
            Follow result = followRepository.save(follow);
            if (result == null) {
                return 0;
            }
            return 1;
        }
    }

    @Override
    public List<MemberDto> fetchFollowings(Long memberId) {

        Member member = findByMemberId(memberId);
        List<Follow> data = member.getFollowers();
        List<MemberDto> result = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            Member following = data.get(i).getFollowing();
            following.toString();
            MemberDto memberDTO = new MemberDto();
            memberDTO.setMemberId(following.getMemberId());
            memberDTO.setGrade(following.getGrade());
            memberDTO.setAge(following.getAge());
            memberDTO.setName(following.getName());
            memberDTO.setNickName(following.getNickName());
            result.add(memberDTO);
        }
        return result;
    }

    @Override
    public List<MemberDto> fetchFollowers(Long memberId) {

        Member member = findByMemberId(memberId);
        List<Follow> data = member.getFollowings();
        List<MemberDto> result = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            Member follower = data.get(i).getFollower();
            follower.toString();
            MemberDto memberDTO = new MemberDto();
            memberDTO.setMemberId(follower.getMemberId());
            memberDTO.setGrade(follower.getGrade());
            memberDTO.setAge(follower.getAge());
            memberDTO.setName(follower.getName());
            memberDTO.setNickName(follower.getNickName());
            result.add(memberDTO);
        }
        return result;
    }
}
