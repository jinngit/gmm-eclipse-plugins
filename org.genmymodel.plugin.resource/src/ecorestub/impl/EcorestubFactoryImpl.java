/**
 */
package ecorestub.impl;

import ecorestub.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EcorestubFactoryImpl extends EFactoryImpl implements EcorestubFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EcorestubFactory init() {
		try {
			EcorestubFactory theEcorestubFactory = (EcorestubFactory)EPackage.Registry.INSTANCE.getEFactory(EcorestubPackage.eNS_URI);
			if (theEcorestubFactory != null) {
				return theEcorestubFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EcorestubFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EcorestubFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case EcorestubPackage.EMODEL_ELEMENT_STUB: return createEModelElementStub();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EModelElementStub createEModelElementStub() {
		EModelElementStubImpl eModelElementStub = new EModelElementStubImpl();
		return eModelElementStub;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EcorestubPackage getEcorestubPackage() {
		return (EcorestubPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EcorestubPackage getPackage() {
		return EcorestubPackage.eINSTANCE;
	}

} //EcorestubFactoryImpl
