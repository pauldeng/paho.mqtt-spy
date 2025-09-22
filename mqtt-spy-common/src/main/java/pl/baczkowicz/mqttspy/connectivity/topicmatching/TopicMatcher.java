/***********************************************************************************
 * 
 * Copyright (c) 2014 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 *    
 * The Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.mqttspy.connectivity.topicmatching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for matching topics against subscriptions, and
 * figure out which subscription the message has been received for.
 */
public class TopicMatcher
{
	/** Diagnostic logger. */
	private static final Logger logger = LoggerFactory.getLogger(TopicMatcher.class);
	
	/** All topics that are in the store. */
	private Set<String> topics = new HashSet<>();
	
	/**
	 * Returns matching subscriptions for the given topic.
	 * 
	 * @param topic The topic to get active subscriptions for
	 * 
	 * @return List of subscription topics matching the given topic
	 */
	public List<String> getMatchingSubscriptions(final String topic)
	{		
		final List<String> matchingSubscriptionTopics = new ArrayList<String>();
		for (final String subscription : topics)
		{
			if (matches(subscription, topic))
			{
				matchingSubscriptionTopics.add(subscription);
			}
		}
		return matchingSubscriptionTopics;
	}

	/**
	 * Adds the given topic to the subscription store - used for topic to subscription matching.
	 *  
	 * @param topic Topic to add
	 */
	public void addSubscriptionToStore(final String topic, final String clientId)
	{
		if (!topics.contains(topic))
		{
			logger.debug("Added subscription " + topic + " (" + clientId + ") to store");
			topics.add(topic);
		}
	}
	
	/**
	 * Removes the given topic from the subscription store - used for topic to subscription matching.
	 *  
	 * @param topic Topic to remove
	 */
	public void removeSubscriptionFromStore(final String topic, final String clientId)
	{
		topics.remove(topic);
	}

	private boolean matches(final String filter, final String topic)
	{
		final String[] filterLevels = filter.split("/");
		final String[] topicLevels = topic.split("/");
		int fi = 0;
		int ti = 0;
		while (fi < filterLevels.length)
		{
			final String filterLevel = filterLevels[fi];
			if ("#".equals(filterLevel))
			{
				return true;
			}
			if (ti >= topicLevels.length)
			{
				return false;
			}
			if (!"+".equals(filterLevel) && !filterLevel.equals(topicLevels[ti]))
			{
				return false;
			}
			fi++;
			ti++;
		}
		return ti == topicLevels.length;
	}
}
