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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs MQTT topic to subscription matching without relying on the legacy
 * moquette dependency that is no longer published. It keeps track of the
 * registered subscription filters and evaluates MQTT wildcard rules locally.
 */
public class TopicMatcher
{
	/** Diagnostic logger. */
	private static final Logger logger = LoggerFactory.getLogger(TopicMatcher.class);

	/** All topics that are currently registered for matching. */
	private final Set<String> topics = new LinkedHashSet<>();

	/**
	 * Returns matching subscriptions for the given topic.
	 *
	 * @param topic The topic to get active subscriptions for
	 *
	 * @return List of subscription topics matching the given topic
	 */
	public List<String> getMatchingSubscriptions(final String topic)
	{
		final List<String> matchingSubscriptionTopics = new ArrayList<>();

		for (final String filter : topics)
		{
			if (matches(filter, topic))
			{
				matchingSubscriptionTopics.add(filter);
			}
		}

		return matchingSubscriptionTopics;
	}

	/**
	 * Adds the given topic to the store - used for topic to subscription matching.
	 *
	 * @param topic Topic filter to add
	 */
	public void addSubscriptionToStore(final String topic, final String clientId)
	{
		if (topics.add(topic))
		{
			logger.debug("Added subscription " + topic + " (" + clientId + ") to store");
		}
	}

	/**
	 * Removes the given topic from the store - used for topic to subscription matching.
	 *
	 * @param topic Topic filter to remove
	 */
	public void removeSubscriptionFromStore(final String topic, final String clientId)
	{
		if (topics.remove(topic))
		{
			logger.debug("Removed subscription " + topic + " (" + clientId + ") from store");
		}
	}

	private boolean matches(final String filter, final String topic)
	{
		if (Objects.equals(filter, topic))
		{
			return true;
		}

		if (filter == null || topic == null)
		{
			return false;
		}

		final String[] filterLevels = filter.split("/", -1);
		final String[] topicLevels = topic.split("/", -1);

		int topicIndex = 0;
		for (int filterIndex = 0; filterIndex < filterLevels.length; filterIndex++)
		{
			final String filterLevel = filterLevels[filterIndex];

			if ("#".equals(filterLevel))
			{
				return true;
			}

			if (topicIndex >= topicLevels.length)
			{
				return false;
			}

			final String topicLevel = topicLevels[topicIndex];

			if (!"+".equals(filterLevel) && !filterLevel.equals(topicLevel))
			{
				return false;
			}

			topicIndex++;
		}

		return topicIndex == topicLevels.length;
	}
}
