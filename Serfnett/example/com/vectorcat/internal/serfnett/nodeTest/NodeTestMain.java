package com.vectorcat.internal.serfnett.nodeTest;

import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractIdleService;
import com.vectorcat.serfnett.api.Service;
import com.vectorcat.serfnett.api.ServiceNode;
import com.vectorcat.serfnett.api.ServiceProvider;
import com.vectorcat.serfnett.ext.ProviderFunnel;
import com.vectorcat.serfnett.ext.ProviderSwitch;
import com.vectorcat.serfnett.ext.ServiceFilter;
import com.vectorcat.serfnett.ext.SimpleServer;
import com.vectorcat.serfnett.tool.ServiceNetworkTool;

public class NodeTestMain {

	static class FakeService extends AbstractIdleService implements Service {
		private final String name;

		public FakeService(String name) {
			this.name = name;
		}

		public FakeService(String name, boolean whiteListed) {
			this(name);
			if (whiteListed) {
				clientWhiteList.add(name);
			}
		}

		@Override
		protected void shutDown() throws Exception {
		}

		@Override
		protected void startUp() throws Exception {
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static boolean systemOK = true;

	static Set<String> clientWhiteList = Sets.newLinkedHashSet();

	static int preferredProcessor = 3;

	private static Function<List<ServiceProvider>, ServiceProvider> createBackupSwitchSelector() {
		return new Function<List<ServiceProvider>, ServiceProvider>() {
			@Override
			public ServiceProvider apply(List<ServiceProvider> input) {
				return systemOK ? input.get(0) : input.get(1);
			}
		};
	}

	private static Predicate<Service> createClientAccessPredicate() {
		return new Predicate<Service>() {
			@Override
			public boolean apply(Service input) {
				FakeService fakeService = (FakeService) input;
				return clientWhiteList.contains(fakeService.name);
			}
		};
	}

	private static Function<List<ServiceProvider>, ServiceProvider> createLoadBalancerSelector() {
		return new Function<List<ServiceProvider>, ServiceProvider>() {
			@Override
			public ServiceProvider apply(List<ServiceProvider> input) {
				return input.get(preferredProcessor);
			}
		};
	}

	public static void main(String[] args) {

		// Exaggerated setup of nodes

		SimpleServer controlServer = new SimpleServer("Control Server");
		controlServer.addService(new FakeService("Login", true));
		controlServer.addService(new FakeService("Game List", true));
		controlServer.addService(new FakeService("Game Downloader", true));
		controlServer.addService(new FakeService("AI Manager", true));
		controlServer.addService(new FakeService("Tournament Viewer", true));
		controlServer.addService(new FakeService("Support", true));
		controlServer.addService(new FakeService("Chat Room", true));
		controlServer.addService(new FakeService("Moderator Tools"));
		controlServer.addService(new FakeService("Administrator Tools"));

		SimpleServer backupControlServer = new SimpleServer(
				"Backup Control Server");
		backupControlServer.addService(new FakeService("Login (Backup)", true));
		backupControlServer
				.addService(new FakeService("Support (Backup)", true));
		backupControlServer.addService(new FakeService("Chat Room (Backup)",
				true));
		backupControlServer.addService(new FakeService(
				"Administrator Tools (Backup)"));

		Function<List<ServiceProvider>, ServiceProvider> backupSwitchSelector = createBackupSwitchSelector();

		ProviderSwitch backupSwitch = new ProviderSwitch(
				"Control/Backup Switch", ImmutableList.<ServiceProvider> of(
						controlServer, backupControlServer),
				backupSwitchSelector);

		SimpleServer databaseServer = new SimpleServer("Database Server");
		databaseServer.addService(new FakeService("Users"));
		databaseServer.addService(new FakeService("Games"));
		databaseServer.addService(new FakeService("Tournaments"));

		SimpleServer storageServer = new SimpleServer("Storage Server");
		storageServer.addService(new FakeService("System Updates", true));
		storageServer.addService(new FakeService("Game Deliverables"));
		storageServer.addService(new FakeService("AI Deliverables"));
		storageServer.addService(new FakeService("Playback Saves"));

		List<ServiceProvider> processingServers = Lists.newArrayList();

		for (int i = 0; i < 5; i++) {
			SimpleServer processingServer = new SimpleServer(
					"Processing Server " + (i + 1));
			processingServer.addService(new FakeService("Game Simulator "
					+ (i + 1)));
			processingServer.addService(new FakeService("Playback Recorder "
					+ (i + 1)));
			processingServer.addService(new FakeService("Live Feed " + (i + 1),
					true));

			processingServers.add(processingServer);
		}

		ProviderSwitch loadBalancer = new ProviderSwitch("Load Balancer",
				processingServers, createLoadBalancerSelector());

		ProviderFunnel allAccess = new ProviderFunnel("Admin Access",
				ImmutableList.<ServiceProvider> of(backupSwitch,
						databaseServer, storageServer, loadBalancer));

		ServiceFilter clientAccess = new ServiceFilter("Client Access",
				allAccess, createClientAccessPredicate());

		ServiceNetworkTool serviceNetworkTool = new ServiceNetworkTool(
				ImmutableList.<ServiceNode> of(clientAccess));

		serviceNetworkTool.show();
	}
}
