package ece1779.loadBalance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.model.Instance;

import ece1779.commonObjects.CloudWatcher;

public class CloudWatching {

	private AmazonCloudWatch cw;
	private BasicAWSCredentials awsCredentials;

	public CloudWatching(BasicAWSCredentials awsCredentials) {
		this.awsCredentials = awsCredentials;
		cw = new AmazonCloudWatchClient(awsCredentials);
	}

	public List<CloudWatcher> getCPUUtilization()
			throws AmazonServiceException, AmazonClientException {
		List<CloudWatcher> statistics = new ArrayList<CloudWatcher>();

		ListMetricsRequest listMetricsRequest = new ListMetricsRequest();
		listMetricsRequest.setMetricName("CPUUtilization");
		listMetricsRequest.setNamespace("AWS/EC2");
		ListMetricsResult result = cw.listMetrics(listMetricsRequest);
		List<Metric> metrics = result.getMetrics();
		for (Metric metric : metrics) {
			CloudWatcher cloudWatcher = getStatistics(metric, cw);
			if (cloudWatcher != null) {
				statistics.add(cloudWatcher);
			}

		}

		return statistics;
	}

	private CloudWatcher getStatistics(Metric metric, AmazonCloudWatch cw)
			throws AmazonServiceException, AmazonClientException {

		String namespace = metric.getNamespace();
		String metricName = metric.getMetricName();
		List<Dimension> dimensions = metric.getDimensions();
		GetMetricStatisticsRequest statisticsRequest = new GetMetricStatisticsRequest();
		statisticsRequest.setNamespace(namespace);
		statisticsRequest.setMetricName(metricName);
		statisticsRequest.setDimensions(dimensions);
		Date endTime = new Date();
		Date startTime = new Date();
		startTime.setTime(endTime.getTime() - 1200000);
		statisticsRequest.setStartTime(startTime);
		statisticsRequest.setEndTime(endTime);
		statisticsRequest.setPeriod(300); // it doesn't matter what is the
											// number here..
		Vector<String> statistics = new Vector<String>();
		statistics.add("Maximum");
		statisticsRequest.setStatistics(statistics);
		GetMetricStatisticsResult stats = cw
				.getMetricStatistics(statisticsRequest);
		System.out.println("[CloudWatching] : Namespace = " + namespace
				+ " Metric = " + metricName + " Dimensions = " + dimensions);
		System.out.println("[CloudWatching] Values = " + stats.toString());

		return parseStatistics(dimensions, stats);
	}

	private CloudWatcher parseStatistics(List<Dimension> dimensions,
			GetMetricStatisticsResult stats) throws AmazonServiceException,
			AmazonClientException {
		List<Datapoint> datapoints = stats.getDatapoints();

		if (datapoints.size() == 0) {
			return null;
		} else {
			return new CloudWatcher(dimensions.get(0).getValue(), datapoints);
		}
	}

	public List<Instance> getAllEC2instances() throws AmazonServiceException,
			AmazonClientException {

		InstancesOperations op = new InstancesOperations(this.awsCredentials);
		return op.getAllEC2instances();
	}

	public static void main(String[] args) {
		String accessKey = "";
		String secretKey = "";
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,
				secretKey);

		CloudWatching cloud = new CloudWatching(awsCredentials);
		List<CloudWatcher> result = cloud.getCPUUtilization();

		for (CloudWatcher w : result) {
			System.out.println("instance id :" + w.getInstanceId());
			System.out.println("namespace " + w.getNameSpace() + "||"
					+ "statistic" + w.getStatistic());
			System.out.println("datapoints:" + w.getDatapoints().toString());
		}
	}
}
