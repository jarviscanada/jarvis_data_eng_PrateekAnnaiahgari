package ca.jrvs.apps.twitter.service;

import ca.jrvs.apps.twitter.Util.JsonUtil;
import ca.jrvs.apps.twitter.dao.CrdDao;
import ca.jrvs.apps.twitter.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TwitterService implements Service {

  private static final int TWEET_TEXT_LIMIT = 140;
  private CrdDao dao;

  public TwitterService(CrdDao dao) {
    this.dao = dao;
  }

  @Override
  public Tweet postTweet(Tweet tweet) {
    validatePostTweet(tweet);
    return (Tweet) dao.create(tweet);
  }

  private void validatePostTweet(Tweet tweet) {
    if (tweet.getText().length() > TWEET_TEXT_LIMIT) {
      throw new IllegalArgumentException("Text length exceeds Tweet limit of 140 Characters");
    }
    if (tweet.getCoordinates() != null) {
      double lon = tweet.getCoordinates().getCoordinates().get(0);
      double lat = tweet.getCoordinates().getCoordinates().get(1);
      if (lon > 180 || lon < -180 || lat < -90 || lat > 90) {
        throw new IllegalArgumentException("Invalid Coordinates");
      }
    }
  }

  @Override
  public Tweet showTweet(String id, String[] fields) {
    validateShowTweet(id, fields);
    Tweet tweet = (Tweet) dao.findById(id);
    if (fields != null) {
      try {
        return JsonUtil.retainFields(tweet, fields);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Error while processing Tweet returned by Get By Id");
      }
    }
    return tweet;
  }

  private void validateShowTweet(String id, String[] fields) {
    String[] validFields = {
        "created_at",
        "id",
        "id_str",
        "text",
        "entities",
        "coordinates",
        "retweet_count",
        "favorite_count",
        "favorited",
        "retweeted"
    };
    validateId(id);
    if (fields != null) {
      for (String field : fields) {
        if (!Arrays.asList(validFields).contains(field)) {
          throw new IllegalArgumentException("Invalid field name : " + field);
        }
      }
    }
  }

  private void validateId(String id) {
    if (!id.matches("[0-9]+")) {
      throw new IllegalArgumentException("Invalid tweet Id");
    }
  }


  @Override
  public List<Tweet> deleteTweets(String[] ids) {
    for (String id : ids) {
      validateId(id);
    }
    List<Tweet> tweets = Arrays.stream(ids).map(id -> (Tweet) dao.deleteById(id)).collect(
        Collectors.toList());
    return tweets;
  }
}
