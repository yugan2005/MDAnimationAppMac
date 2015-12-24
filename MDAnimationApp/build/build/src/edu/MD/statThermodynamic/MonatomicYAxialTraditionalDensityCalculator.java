package edu.MD.statThermodynamic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.utility.IterableCounter;
import edu.MD.utility.MDConstants;
import edu.MD.utility.YComparator;

public class MonatomicYAxialTraditionalDensityCalculator implements IDensityCalculator {
	//TODO need write testing case
	private int nBIns;
	private MDVector systemBoundary;
	private List<MDNumber> yPositions;
	private MDNumber binVolume;
	private MDNumber densityConstant;
	private int averageStep = 200;
	private ArrayDeque<List<MDNumber>> densityQueue = new ArrayDeque<>(averageStep);
	private int totalNumOfParticles = -1;
	private List<MDNumber> averageDensityProfile;

	private MonatomicYAxialTraditionalDensityCalculator(int numBins, MDVector systemBoundaryInput) {
		this.nBIns = numBins;
		this.systemBoundary = systemBoundaryInput;
		yPositions = new ArrayList<>(nBIns + 1);
		MDNumber crossArea = systemBoundary.getCartesianComponent()[0].times(systemBoundary.getCartesianComponent()[2]);
		MDNumber binLength = systemBoundary.getCartesianComponent()[1].divide(nBIns);
		binVolume = binLength.times(crossArea);
		yPositions = new ArrayList<>(nBIns + 1);
		for (int i = 0; i <= nBIns; i++) {
			yPositions.add(binLength.times(i));
		}
		densityConstant = binVolume.times(MDConstants.AVOGADRO).pow(-1);
	}

	public static IDensityCalculator getInstance(int numBins, MDVector systemBoundary) {
		MonatomicYAxialTraditionalDensityCalculator instance = new MonatomicYAxialTraditionalDensityCalculator(numBins,
				systemBoundary);
		return instance;
	}

	public List<List<MDNumber>> calculate(Iterable<MDVector> positions) {
		
		if (totalNumOfParticles == -1) {
			totalNumOfParticles = IterableCounter.count(positions);
		}
		
		List<MDVector> listOfPositions;
		if (positions instanceof Collection<?>) {
			Collection<MDVector> collectionOfPositions = (Collection<MDVector>) positions;
			listOfPositions = new ArrayList<MDVector>(collectionOfPositions);
		} else {
			listOfPositions = new ArrayList<>(totalNumOfParticles);
			for (MDVector position : positions) {
				listOfPositions.add(position);
			}
		}

		listOfPositions.sort(new YComparator());

		int[] particleNumber = new int[nBIns + 1];
		int currentBin = 0;
		for (int i = 0; i < totalNumOfParticles; i++) {
			while (listOfPositions.get(i).getCartesianComponent()[1].compareTo(yPositions.get(currentBin)) >= 0) {
				currentBin++;
			}
			particleNumber[currentBin - 1] += 1;
		}
		particleNumber[nBIns] = particleNumber[0];

		List<MDNumber> densityProfile = new ArrayList<>(nBIns + 1);
		for (int i = 0; i < nBIns + 1; i++) {
			densityProfile.add(densityConstant.times(particleNumber[i]));
		}

		if (densityQueue.isEmpty()) {
			averageDensityProfile = densityProfile;
		} else if (densityQueue.size() < averageStep) {
			int currentSize = densityQueue.size();
			for (int i = 0; i < nBIns; i++) {
				MDNumber averageLocalDensity = averageDensityProfile.get(i).times(currentSize)
						.plus(densityProfile.get(i)).divide(currentSize + 1);
				averageDensityProfile.set(i, averageLocalDensity);
			}
			densityQueue.addLast(densityProfile);
		} else {
			List<MDNumber> removedDensityProfile = densityQueue.removeFirst();
			for (int i = 0; i < nBIns; i++) {
				MDNumber averageLocalDensity = averageDensityProfile.get(i).times(averageStep)
						.plus(densityProfile.get(i)).minus(removedDensityProfile.get(i)).divide(averageStep);
				averageDensityProfile.set(i, averageLocalDensity);
			}
			densityQueue.addLast(densityProfile);
		}

		List<List<MDNumber>> result = new ArrayList<>(2);
		result.add(yPositions);
		result.add(averageDensityProfile);
		return result;
	}
}
