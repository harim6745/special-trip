package com.project.mega.triplus.controller;

import com.google.gson.JsonObject;
import com.project.mega.triplus.entity.*;
import com.project.mega.triplus.form.JoinForm;
import com.project.mega.triplus.form.PlanForm;
import com.project.mega.triplus.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final PlaceService placeService;

    private final ApiService apiService;

    private final UserService userService;

    private final HttpSession  httpSession;

    private final PlanService planService;

    private final ReviewService reviewService;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        // apiService.loadPlaces();
    }

    @RequestMapping("/")
    public String index(Model model){
        List<Place> placeList = placeService.getPlace();
        List<Plan> planList = planService.getAllByOrderByLikedDesc();

        model.addAttribute("placeList", placeList);
        model.addAttribute("planList", planList);

        return "index";
    }

    @PostMapping("/")
    public String main(Model model){
        List<Place> placeList = placeService.getPlace();
        model.addAttribute("placeList", placeList);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("status", "login");

        return index(model);
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword") String keyword, Model model){
        int rand, cnt = 8;

        List<XMLResponseItem> itemList = apiService.getKeywordResultList(keyword);

        List<XMLResponseItem> attractionList = new ArrayList<>();
        List<XMLResponseItem> foodList = new ArrayList<>();
        List<XMLResponseItem> shopList = new ArrayList<>();
        List<XMLResponseItem> festivalList = new ArrayList<>();

        for(XMLResponseItem item : itemList){
            switch (item.getContentTypeId()){
                case "12":
                    attractionList.add(item);
                    break;
                case "39":
                    foodList.add(item);
                    break;
                case "38":
                    shopList.add(item);
                    break;
                case "15":
                    festivalList.add(item);
                default:
                    break;
            }
        }


        model.addAttribute("keyword", keyword);

        rand = Math.max((int)(Math.random() * (attractionList.size() - cnt)), 0);
        model.addAttribute("attractionList", attractionList.subList(rand, Math.min(rand + cnt, attractionList.size())));

        rand = Math.max((int)(Math.random() * (foodList.size() - cnt)), 0);
        model.addAttribute("foodList", foodList.subList(rand, Math.min(rand + cnt, foodList.size())));

        rand = Math.max((int)(Math.random() * (shopList.size() - cnt)), 0);
        model.addAttribute("shopList", shopList.subList(rand, Math.min(rand + cnt, shopList.size())));

        rand = Math.max((int)(Math.random() * (festivalList.size() - cnt)), 0);
        model.addAttribute("festivalList", festivalList.subList(rand, Math.min(rand + cnt, festivalList.size())));


        return "view/search";
    }

    @GetMapping("/detail")
    public String detail(
            @CurrentUser User user,
            @RequestParam(value = "content_id") String contentId, Model model){
        String radius = "50000";
        Place place;

        int rand, cnt = 5;

        XMLResponseItem item = apiService.getItemByContentId(contentId);
        List<XMLResponseItem> recommendPlaces_attraction = apiService.getItemByMapXAndMapY(item.getMapX(), item.getMapY(), radius, "12");
        List<XMLResponseItem> recommendPlaces_food = apiService.getItemByMapXAndMapY(item.getMapX(), item.getMapY(), radius, "39");
        place = placeService.getPlaceByContentId(contentId);

        model.addAttribute("item", item);
        model.addAttribute("reviews", place.getReviews());
        model.addAttribute("content_id", contentId);
        model.addAttribute("user", user);

        rand = Math.max((int) (Math.random() * (recommendPlaces_attraction.size() - cnt)), 0);
        model.addAttribute("recommendPlaces_attraction", recommendPlaces_attraction.subList(rand, rand + Math.min(recommendPlaces_attraction.size(), cnt)));

        rand = Math.max((int) (Math.random() * (recommendPlaces_food.size() - cnt)), 0);
        model.addAttribute("recommendPlaces_food", recommendPlaces_food.subList(rand, rand + Math.min(recommendPlaces_food.size(), cnt)));

        Place like = placeService.getPlaceByContentId(contentId);
        model.addAttribute("like", like);

        return "view/detail";
    }

    @PostMapping("/register_review")
    public String review(
            @CurrentUser User user, Model model,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "content_id") String contentId){

        Place place = placeService.getPlaceByContentId(contentId);
        placeService.saveReview(user, place, content);

        return detail(user,contentId,model);
    }


    @GetMapping("/plan")
    public String plan(Model model, @RequestParam(value = "plan_id", required = false)String planId){
        Plan plan;

        if(planId != null){
            try{
                plan = planService.getPlanById(Long.parseLong(planId));
                model.addAttribute("plan", plan);
            } catch (Exception e){
                log.error("no plan");
            }
        }


        model.addAttribute("placeList", placeService.addPlaces());

        return "view/plan";
    }


    @GetMapping("/detail/remove")
    @ResponseBody
    public String removeReview(@RequestParam(value = "id") String reviewId){

        reviewService.deleteReviewById((Long.parseLong(reviewId)));

        return "done";
    }

    // mypage remove review
    @GetMapping("/mypage/remove")
    @ResponseBody
    public String removeMyPageReview(@RequestParam(value = "id") String reviewId){

        reviewService.deleteReviewById((Long.parseLong(reviewId)));

        return "done";
    }

    @GetMapping("/detail/like")
    @ResponseBody
    public String addLike(@CurrentUser User user,
                @RequestParam(value = "content_id") String contentId){

        JsonObject object = new JsonObject();

        try {
            placeService.addLikes(user, contentId);
            object.addProperty("result", true);
            object.addProperty("message", "찜 목록에 등록되었습니다.");
        } catch (IllegalStateException e){
            object.addProperty("result", false);
            object.addProperty("message", e.getMessage());
        }
        return object.toString();
    }

    @GetMapping("/detail/dislike")
    @ResponseBody
    public String dislike(@CurrentUser User user,
                          @RequestParam(value = "content_id") String contentId){

        JsonObject object = new JsonObject();

        try {
            placeService.disLikes(user, contentId);
            object.addProperty("result", true);
            object.addProperty("message", "찜 해제.");
        } catch (IllegalStateException e){
            object.addProperty("result", false);
            object.addProperty("message", e.getMessage());
        }

        return object.toString();
    }

    @GetMapping("/mypage")
    public String mypage(@CurrentUser User user, Model model){
        if(user == null ){
            user = (User)httpSession.getAttribute("user");
        }

        model.addAttribute("user", user);

        List<Plan> planList = userService.getPlanList(user);
        model.addAttribute("planList", planList);

        List<Review> reviewList = userService.getReviewList(user);
        model.addAttribute("reviewList", reviewList);

        List<Place> likeList = userService.getLikeList(user);

        List<Place> attractionList = new ArrayList<>();
        List<Place> foodList = new ArrayList<>();
        List<Place> shopList = new ArrayList<>();
        List<Place> festivalList = new ArrayList<>();

        for(Place like : likeList){
            switch (like.getContentType()){
                case "12":
                    attractionList.add(like);
                    break;
                case "39":
                    foodList.add(like);
                    break;
                case "38":
                    shopList.add(like);
                    break;
                case "15":
                    festivalList.add(like);
                default:
                    break;
            }
        }


        model.addAttribute("attractionList", attractionList);
        model.addAttribute("foodList", foodList);
        model.addAttribute("shopList", shopList);
        model.addAttribute("festivalList", festivalList);


       return "view/mypage";
    }

    @PostMapping("/delete_user")
    public String deleteUser(@CurrentUser User user){
        userService.deleteUser(user);

        return "redirect:/";
    }

    // 회원정보 수정
    @PostMapping("/update_info")
    public String updateUser(@RequestParam(value = "email")String email,
                             @RequestParam(value = "nickName")String nickName,
                             Model model){
        userService.updateUser(email, nickName);

        return "redirect:/mypage";
    }

    @PostMapping("/mypage/checkPw")
    @ResponseBody
    public String checkPw(@CurrentUser User user, @RequestParam(value = "oldPw") String oldPw){
        String result=null;

        if (passwordEncoder.matches(oldPw, user.getPassword())){
            result="pwConfirmOK";
        } else {
            result="pwConfirmNo";
        }
        return result;
    }

    @PostMapping("/mypage/change_password")
    @ResponseBody
    public String pwChange(@CurrentUser User user, @RequestParam(value = "newPw") String newPw){
        userService.changePassword(user, newPw);
        userService.login(user);

        return "changeSuccess";
    }

    @GetMapping("/total_plan")
    public String totalPlan(Model model){
        List<Plan> allPlans = planService.getAllPlans();

        Set<String> citySet = new HashSet<>(Arrays.asList("1", "2", "31", "32", "6", "7", "4", "5", "3", "38", "39"));

        model.addAttribute("planList", allPlans);

        return "view/total_plan";
    }

    @GetMapping("/total_place")
    public String totalPlace(Model model, @PageableDefault Pageable pageable,
                             @RequestParam(value = "cat", required = false) String cat){

        Page<Place> allPlaceList = placeService.getPlaceList(pageable, "12");
        Page<Place> seoulPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "1");
        Page<Place> incheonPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "2");
        Page<Place> ulsanPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "7");
        Page<Place> gyeonggiPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "31");
        Page<Place> busanPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "6");
        Page<Place> daeguPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "4");
        Page<Place> gwangjuPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "5");
        Page<Place> daejeonPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "3");
        Page<Place> jejuPlaceList = placeService.getPlaceListEachAreaCode(pageable, "12", "39");

        model.addAttribute("allPlaceList", allPlaceList);
        model.addAttribute("seoulPlaceList",seoulPlaceList);
        model.addAttribute("incheonPlaceList",incheonPlaceList);
        model.addAttribute("ulsanPlaceList",ulsanPlaceList);
        model.addAttribute("gyeonggiPlaceList",gyeonggiPlaceList);
        model.addAttribute("busanPlaceList",busanPlaceList);
        model.addAttribute("daeguPlaceList",daeguPlaceList);
        model.addAttribute("gwangjuPlaceList",gwangjuPlaceList);
        model.addAttribute("daejeonPlaceList",daejeonPlaceList);
        model.addAttribute("jejuPlaceList",jejuPlaceList);
        model.addAttribute("cat", cat);

        return "view/total_place";
    }

    @RequestMapping("/access_denied")
    public String accessDenied() {

        return "view/access_denied";
    }

    @PostMapping("/plan/save")
    @ResponseBody
    public String savePlan(@CurrentUser User user,
            @RequestBody PlanForm planForm){

        Plan plan = new Plan();
        Long planId = planForm.getPlanId();

        if(planId > 0){
            plan = planService.getPlanById(planId);
        }

        return Long.toString(planService.savePlan(user, plan, planForm));
    }


    @GetMapping("/mypage/myplan")
    @ResponseBody
    public Plan myPlan(@CurrentUser User user,
                         @RequestParam(value = "id") Long id ){
        Plan planById = planService.getPlanById(id);
        System.out.println(planById);
        return planById;
    }

    @PostMapping("/header/checkNickName")
    @ResponseBody
    public String checkNickName(@RequestParam(value = "nickNameCheck")String nickName){
        String message=null;

        if(userService.existsNickName(nickName)){
            message="nickNameNO";
        } else{
            message="nickNameOK";
        }

        return message;
    }

    @PostMapping("/header/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam(value = "emailCheck")String email){
        String message=null;

        if(userService.existsEmail(email)){
            message="emailNO";
        } else{
            message="emailOK";
        }

        return message;
    }

    @Transactional
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ResponseBody
    public String joinSubmit(
            @RequestParam(value = "joinNickname") String nickname,
            @RequestParam(value = "joinEmail") String email,
            @RequestParam(value = "joinPassword") String password,
            @RequestParam(value = "checklist") String checklist
    ){
        JoinForm joinForm = new JoinForm();

        joinForm.setNickname(nickname);
        joinForm.setEmail(email);
        joinForm.setPassword(password);
        joinForm.setAgreeTermsOfService(checklist);

        User newUser = userService.processNewUser(joinForm);
        userService.login(newUser);

        return "joinSuccess";
    }

    @PostMapping("/login")
    public String login(@RequestParam(value = "username")String email, @RequestParam(value = "password")String password, Model model){
        if(userService.loginProcess(email, password)){
            return "index";
        } else{
            return "redirect:/login?error";
        }
    }

}
