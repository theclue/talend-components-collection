package org.gabrielebaldassarre.twitter.stream.tweet.listener;

import java.util.Observable;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class MyStatusListener extends Observable implements StatusListener {

	public MyStatusListener() {

	}

	public void onException(Exception ex) {
	}

	public void onStatus(Status status) {
		setChanged();
		notifyObservers(status);
	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

	}

	public void onScrubGeo(long userId, long upToStatusId) {

	}

	public void onStallWarning(StallWarning warning) {

	}

}
