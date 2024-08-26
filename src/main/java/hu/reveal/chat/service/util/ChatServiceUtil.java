package hu.reveal.chat.service.util;

import hu.reveal.chat.enums.Gender;
import hu.reveal.chat.enums.Topic;
import hu.reveal.chat.model.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;


public class ChatServiceUtil {

    private ChatServiceUtil() {
    }

    public static Topic getRandomTopic(User userOne, User userTwo) {

        ArrayList<Topic> commonTopics = new ArrayList<>();
        for (Topic userOneTopic : userOne.topics()) {
            for (Topic UserTwoTopic : userTwo.topics()) {
                if (userOneTopic.equals(UserTwoTopic)) {
                    commonTopics.add(userOneTopic);
                }
            }
        }
        return commonTopics.get(new Random().nextInt(commonTopics.size()));
    }

    public static ArrayList<User> getFilteredUsersByTopics(
            ArrayList<Topic> userOneTopics,
            ArrayList<User> possibleUsers
    ) {

        ArrayList<User> resultUsers = new ArrayList<>();
        int maxCommonTopics = 0;

        for (User possibleUser : possibleUsers) {
            int commonTopicsCount = 0;
            for (Topic possibleUserTopic : possibleUser.topics()) {
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

    public static Optional<User> getUserToAge(int age, ArrayList<User> userList) {
        Optional<User> resultUser = Optional.empty();
        Integer min_diff = null;

        for (User user : userList) {
            int diff = Math.abs(age - user.age());

            if (min_diff == null || diff < min_diff) {
                min_diff = diff;
                resultUser = Optional.of(user);
            }
        }
        return resultUser;
    }

    public static boolean isMatchAgeRequests(User userOne, User userTwo) {

        if (userOne.age() >= userTwo.partnerMinAge()
                && userOne.age() <= userTwo.partnerMaxAge()) {

            return userTwo.age() >= userOne.partnerMinAge()
                    && userTwo.age() <= userOne.partnerMaxAge();
        }
        return false;
    }

    public static boolean isMatchGenderRequests(User userOne, User userTwo) {

        if (userOne.partnerGender().equals(userTwo.gender())
                && userTwo.partnerGender().equals(userOne.gender())) {
            return true;
        }
        if (userOne.partnerGender().equals(Gender.MALE)
                && userTwo.gender().equals(Gender.FEMALE)) {
            return false;
        }

        if (userOne.partnerGender().equals(Gender.FEMALE)
                && userTwo.gender().equals(Gender.MALE)) {
            return false;
        }
        if (userTwo.partnerGender().equals(Gender.MALE)
                && userOne.gender().equals(Gender.FEMALE)) {
            return false;
        }

        if (userTwo.partnerGender().equals(Gender.FEMALE)
                && userOne.gender().equals(Gender.MALE)) {
            return false;
        }
        return true;
    }
}