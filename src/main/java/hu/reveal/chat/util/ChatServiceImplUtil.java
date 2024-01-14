package hu.reveal.chat.service.util;

import hu.reveal.chat.enums.Gender;
import hu.reveal.chat.enums.Topic;
import hu.reveal.chat.model.User;

import java.util.*;

public class ChatServiceImplUtil {

    public Topic getRandomTopic(User userOne, User userTwo) {

        List<Topic> commonTopics = new ArrayList<>();
        for (Topic userOneTopic : userOne.getTopics()) {
            for (Topic UserTwoTopic : userTwo.getTopics()) {
                if (userOneTopic.equals(UserTwoTopic)) {
                    commonTopics.add(userOneTopic);
                }
            }
        }
        return commonTopics.get(new Random().nextInt(commonTopics.size()));
    }

    public List<User> getFilteredUsersByTopics(List<Topic> userOneTopics, List<User> possibleUsers) {

        List<User> resultUsers = new ArrayList<>();
        int maxCommonTopics = 0;

        for (User possibleUser : possibleUsers) {
            int commonTopicsCount = 0;
            for (Topic possibleUserTopic : possibleUser.getTopics()) {
                for (Topic userOneTopic : userOneTopics) {
                    if (possibleUserTopic.equals(userOneTopic)) {
                       commonTopicsCount++;
                    }
                }
            }
            if (commonTopicsCount > maxCommonTopics) {
                resultUsers.clear();
                resultUsers.add(possibleUser);
                maxCommonTopics = commonTopicsCount;

            } else if (commonTopicsCount == maxCommonTopics) {
                resultUsers.add(possibleUser);
            }
        }
        return resultUsers;
    }
    public Optional<User> getUserToAge(int age, List<User> userList) {
        Optional<User> resultUser = Optional.empty();
        Integer min_diff = null;

        for (User user : userList) {
            int diff = Math.abs(age - user.getAge());

            if (Objects.isNull(min_diff) || diff < min_diff) {
                min_diff = diff;
                resultUser = Optional.of(user);
            }
        }
        return resultUser;
    }

    public boolean isMatchAgeRequests(User userOne, User userTwo) {

        if (userOne.getAge() >= userTwo.getPartnerMinAge()
                && userOne.getAge() <= userTwo.getPartnerMaxAge()) {

            return userTwo.getAge() >= userOne.getPartnerMinAge()
                    && userTwo.getAge() <= userOne.getPartnerMaxAge();
        }
        return false;
    }

    public boolean isMatchGenderRequests(User userOne, User userTwo) {

        // MALE - MALE / FEMALE - FEMALE / BOTH - BOTH /
        if (userOne.getPartnerGender().equals(userTwo.getGender())
            && userTwo.getPartnerGender().equals(userOne.getGender())) {
            return true;
        }
        //nem egyezik
        if (userOne.getPartnerGender().equals(Gender.MALE)
            && userTwo.getGender().equals(Gender.FEMALE)) {
            return false;
        }

        if (userOne.getPartnerGender().equals(Gender.FEMALE)
                && userTwo.getGender().equals(Gender.MALE)) {
            return false;
        }
        //user1-nek jó
        if (userTwo.getPartnerGender().equals(Gender.MALE)
                && userOne.getGender().equals(Gender.FEMALE)) {
            return false;
        }

        if (userTwo.getPartnerGender().equals(Gender.FEMALE)
                && userOne.getGender().equals(Gender.MALE)) {
            return false;
        }
        return true;
    }
}
